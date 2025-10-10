// =====================================================
// Función para hacer un preview (vista previa) de una imagen
// =====================================================
function mostrarImagen(input) {
    if (input.files && input.files[0]) {
        const imagen = input.files[0];
        const maximo = 512 * 1024; // Tamaño máximo: 512 KB

        if (imagen.size <= maximo) {
            const lector = new FileReader();
            lector.onload = function (e) {
                $('#blah').attr('src', e.target.result).height(200);
            };
            lector.readAsDataURL(imagen);
        } else {
            alert("⚠️ La imagen seleccionada es muy grande. No debe superar los 512 KB.");
        }
    }
}

// =====================================================
// Insertar información en el modal según el registro seleccionado
// =====================================================
document.addEventListener('DOMContentLoaded', function () {
    const confirmModal = document.getElementById('confirmModal');

    if (confirmModal) {
        confirmModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const id = button.getAttribute('data-bs-id');
            const descripcion = button.getAttribute('data-bs-descripcion');

            document.getElementById('modalId').value = id;
            document.getElementById('modalDescription').textContent = descripcion;
        });
    }
});

// =====================================================
// Quitar notificaciones tipo "toast" automáticamente
// =====================================================
setTimeout(() => {
    document.querySelectorAll('.toast').forEach(t => t.classList.remove('show'));
}, 4000);
