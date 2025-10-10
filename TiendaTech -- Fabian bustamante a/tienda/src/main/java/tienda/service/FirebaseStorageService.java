package tienda.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FirebaseStorageService {

    @Value("${firebase.bucket.name}")
    private String bucketName;

    @Value("${firebase.storage.path}")
    private String storagePath;

    // Cliente de Firebase Storage inyectado como bean
    private final Storage storage;

    public FirebaseStorageService(Storage storage) {
        this.storage = storage;
    }

    /**
     * Sube un archivo de imagen al almacenamiento de Firebase.
     *
     * @param localFile archivo recibido del cliente
     * @param folder    carpeta destino en Firebase Storage
     * @param id        identificador del recurso (para generar nombre único)
     * @return URL firmada del archivo subido
     * @throws IOException si ocurre un error al subir o manipular el archivo
     */
    public String uploadImage(MultipartFile localFile, String folder, Long id) throws IOException {
        String originalName = localFile.getOriginalFilename();
        String fileExtension = "";

        if (originalName != null && originalName.contains(".")) {
            fileExtension = originalName.substring(originalName.lastIndexOf("."));
        }

        // Genera nombre del archivo con formato consistente
        String fileName = "img" + getFormattedNumber(id) + fileExtension;

        File tempFile = convertToFile(localFile);

        try {
            return uploadToFirebase(tempFile, folder, fileName);
        } finally {
            // Limpieza del archivo temporal
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * Convierte un MultipartFile a un archivo temporal en el servidor.
     */
    private File convertToFile(MultipartFile multipartFile) throws IOException {
        File tempFile = File.createTempFile("upload-", ".tmp");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile;
    }

    /**
     * Sube el archivo al almacenamiento de Firebase y genera una URL firmada.
     */
    private String uploadToFirebase(File file, String folder, String fileName) throws IOException {
        // Definición del ID y la metadata del blob
        BlobId blobId = BlobId.of(bucketName, storagePath + "/" + folder + "/" + fileName);
        String mimeType = Files.probeContentType(file.toPath());
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(mimeType != null ? mimeType : "media")
                .build();

        // Subida del archivo al bucket
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));

        // Generación de URL firmada válida por 5 años
        return storage.signUrl(blobInfo, 1825, TimeUnit.DAYS).toString();
    }

    /**
     * Genera un string numérico con formato de 14 dígitos, rellenado con ceros a la izquierda.
     */
    private String getFormattedNumber(long id) {
        return String.format("%014d", id);
    }
}
