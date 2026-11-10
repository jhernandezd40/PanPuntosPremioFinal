document.addEventListener('DOMContentLoaded', () => {
    const categoria = document.getElementById('categoria');
    const seccionMenu = document.querySelector('.seccion-menu');
    const campoExistencia = document.querySelector('.campo-existencia');

    const actualizar = () => {
        const esMenu = categoria.value === 'MENU';
        seccionMenu.classList.toggle('d-none', !esMenu);
        seccionMenu.querySelectorAll('select').forEach(select => select.disabled = !esMenu);
        if (campoExistencia) {
            campoExistencia.classList.toggle('d-none', esMenu);
        }
    };

    categoria.addEventListener('change', actualizar);
    actualizar();
});
