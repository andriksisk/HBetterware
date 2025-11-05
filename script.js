
// ----- RUTA POR DEFECTO (ajusta si tu archivo se llama distinto o está en assets/) -----
const RUTA_POR_DEFECTO = 'assets/Placeholder.png'; // o 'assets/Placehoder.png' si lo guardaste en assets/

// ----- INVENTARIO -----
const productos = [
    { nombre: "Contenedor multiusos", cantidad: 12, precio: 120, imagen: "https://via.placeholder.com/100" },
    { nombre: "Organizador de cocina", cantidad: 8, precio: 95, imagen: "https://via.placeholder.com/100" },
    { nombre: "Cesta para ropa", cantidad: 5, precio: 150, imagen: "" },
    { nombre: "Botella térmica", cantidad: 20, precio: 80, imagen: "https://via.placeholder.com/100" }
];

// --- Esperar a que el DOM cargue para evitar referencias null ---
document.addEventListener('DOMContentLoaded', () => {

    // ----- ELEMENTOS DEL DOM -----
    const contenedorProductos = document.getElementById("productos"); // grid dentro de inventario
    const buscador = document.getElementById("searchInput");
    const selectPedido = document.getElementById("productoPedido");
    const formPedido = document.getElementById("pedidoForm");
    const mensaje = document.getElementById("mensajeConfirmacion");

    const seccionInventario = document.getElementById("inventario");
    const seccionPedido = document.getElementById("pedido");
    const seccionContacto = document.getElementById("contacto");

    const btnPedido = document.getElementById("btnPedido");
    const btnContacto = document.getElementById("btnContacto");
    const titulo = document.getElementById("titulo");

    // --- NUEVOS ELEMENTOS PARA EL PEDIDO ---
    const tablaPedido = document.getElementById("tablaPedido");
    const cuerpoTabla = tablaPedido ? tablaPedido.querySelector("tbody") : null;
    const mensajeVacio = document.getElementById("mensajeVacio");
    const btnVolverInventario = document.getElementById("btnVolverInventario");

    let pedidoActual = [];

    // ----- FUNCIONES -----
    function crearImagen(src, alt) {
        const img = document.createElement('img');
        img.alt = alt || '';
        img.src = src || RUTA_POR_DEFECTO;
        img.width = 100;
        img.height = 100;
        img.style.objectFit = 'cover';
        img.style.borderRadius = '12px';
        img.onerror = function() {
            if (img.dataset.fallbackDone) return;
            img.dataset.fallbackDone = "true";
            img.src = RUTA_POR_DEFECTO;
        };
        return img;
    }

    function mostrarProductos(lista) {
        // vaciar contenedor
        contenedorProductos.innerHTML = "";
        lista.forEach(p => {
            const div = document.createElement("div");
            div.classList.add("producto");

            const img = crearImagen(p.imagen, p.nombre);

            const info = document.createElement('div');
            info.className = 'producto-info';
            info.innerHTML = `
                <h3>${p.nombre}</h3>
                <p>Cantidad disponible: ${p.cantidad}</p>
                <p class="precio">$${p.precio.toFixed(2)} MXN</p>
            `;

            const btn = document.createElement('button');
            btn.textContent = 'Añadir';
            btn.addEventListener('click', () => agregarAlPedido(p));

            div.appendChild(img);
            div.appendChild(info);
            div.appendChild(btn);

            contenedorProductos.appendChild(div);
        });
    }

    function mostrarSeccion(nombre) {
        // ocultar todas
        seccionInventario.classList.add("oculto");
        seccionPedido.classList.add("oculto");
        seccionContacto.classList.add("oculto");

        // mostrar la solicitada
        if (nombre === "inventario") seccionInventario.classList.remove("oculto");
        if (nombre === "pedido") seccionPedido.classList.remove("oculto");
        if (nombre === "contacto") seccionContacto.classList.remove("oculto");
    }

    // --- FUNCIONES PARA MANEJAR EL PEDIDO ---
    function agregarAlPedido(producto) {
        if (producto.cantidad <= 0) {
            alert(`No hay disponibilidad de ${producto.nombre}.`);
            return;
        }

        // Buscar si ya está en el pedido
        let item = pedidoActual.find(p => p.nombre === producto.nombre);
        if (item) {
            item.cantidadPedida++;
        } else {
            pedidoActual.push({ nombre: producto.nombre, cantidadPedida: 1 });
        }

        producto.cantidad--; // disminuir inventario
        mostrarProductos(productos); // actualizar inventario
        actualizarTablaPedido();
    }

    function actualizarTablaPedido() {
        if (!cuerpoTabla) return;
        cuerpoTabla.innerHTML = "";
        if (pedidoActual.length === 0) {
            if (tablaPedido) tablaPedido.classList.add("oculto");
            if (mensajeVacio) mensajeVacio.classList.remove("oculto");
            if (btnVolverInventario) btnVolverInventario.classList.remove("oculto");
            return;
        }

        if (tablaPedido) tablaPedido.classList.remove("oculto");
        if (mensajeVacio) mensajeVacio.classList.add("oculto");
        if (btnVolverInventario) btnVolverInventario.classList.add("oculto");

        pedidoActual.forEach(item => {
            const producto = productos.find(p => p.nombre === item.nombre);
            const fila = document.createElement("tr");
            fila.innerHTML = `
                <td>${item.nombre}</td>
                <td>${item.cantidadPedida}</td>
                <td>${producto ? producto.cantidad : 0}</td>
            `;
            cuerpoTabla.appendChild(fila);
        });
    }

    // --- BOTÓN PARA VOLVER AL INVENTARIO ---
    if (btnVolverInventario) {
        btnVolverInventario.addEventListener("click", () => {
            mostrarSeccion("inventario");
        });
    }

    // ----- INICIALIZACIÓN -----
    mostrarProductos(productos);

    // llenar select de pedido (si existe)
    if (selectPedido) {
        selectPedido.innerHTML = "";
        productos.forEach(p => {
            const opt = document.createElement("option");
            opt.value = p.nombre;
            opt.textContent = p.nombre;
            selectPedido.appendChild(opt);
        });
    }

    // ----- EVENTOS -----
    if (buscador) {
        buscador.addEventListener("input", e => {
            const texto = e.target.value.toLowerCase();
            const filtrados = productos.filter(p => p.nombre.toLowerCase().includes(texto));
            mostrarProductos(filtrados);
        });
    }

    if (titulo) titulo.addEventListener("click", () => mostrarSeccion("inventario"));
    if (btnPedido) btnPedido.addEventListener("click", () => { actualizarTablaPedido(); mostrarSeccion("pedido"); });
    if (btnContacto) btnContacto.addEventListener("click", () => mostrarSeccion("contacto"));

    // ----- FORMULARIO DE PEDIDO (si aún tienes envío) -----
    if (formPedido) {
        formPedido.addEventListener("submit", e => {
            e.preventDefault();
            const nombre = document.getElementById("nombre").value;
            const producto = selectPedido ? selectPedido.value : '';
            const cantidad = document.getElementById("cantidad").value;
            const direccion = document.getElementById("direccion").value;

            if (mensaje) {
                mensaje.textContent = `✅ Gracias ${nombre}, tu pedido de ${cantidad} ${producto}(s) será enviado a ${direccion}.`;
                mensaje.classList.remove("oculto");
            }
            formPedido.reset();
        });
    }

});
