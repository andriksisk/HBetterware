// ----- INVENTARIO -----
const productos = [
    { id: 1, nombre: "Organizador de Cocina", precio: 250, imagen: "https://cdn.betterware.com.mx/organizador.jpg" },
    { id: 2, nombre: "Contenedor Multiusos", precio: 180, imagen: "https://cdn.betterware.com.mx/contenedor.jpg" },
    { id: 3, nombre: "Escurridor Plegable", precio: 210, imagen: "https://cdn.betterware.com.mx/escurridor.jpg" },
    { id: 4, nombre: "Cesta para Ropa", precio: 300, imagen: "https://cdn.betterware.com.mx/cesta.jpg" },
    { id: 5, nombre: "Porta Toallas Adhesivo", precio: 90, imagen: "https://cdn.betterware.com.mx/portatoallas.jpg" }
];

// ----- ELEMENTOS DEL DOM -----
const contenedor = document.getElementById("productosContainer");
const buscador = document.getElementById("searchInput");
const selectPedido = document.getElementById("productoPedido");
const formPedido = document.getElementById("pedidoForm");
const mensaje = document.getElementById("mensajeConfirmacion");

const seccionProductos = document.getElementById("productos");
const seccionPedido = document.getElementById("pedido");
const seccionContacto = document.getElementById("contacto");

const btnPedido = document.getElementById("btnPedido");
const btnContacto = document.getElementById("btnContacto");
const titulo = document.getElementById("titulo");

// ----- FUNCIONES -----
function mostrarProductos(lista) {
    contenedor.innerHTML = "";
    lista.forEach(prod => {
        const card = document.createElement("div");
        card.classList.add("producto");
        card.innerHTML = `
            <img src="${prod.imagen}" alt="${prod.nombre}">
            <h3>${prod.nombre}</h3>
            <p>$${prod.precio} MXN</p>
        `;
        contenedor.appendChild(card);
    });
}

function mostrarSeccion(nombre) {
    seccionProductos.classList.add("oculto");
    seccionPedido.classList.add("oculto");
    seccionContacto.classList.add("oculto");

    if (nombre === "productos") seccionProductos.classList.remove("oculto");
    if (nombre === "pedido") seccionPedido.classList.remove("oculto");
    if (nombre === "contacto") seccionContacto.classList.remove("oculto");
}

// ----- INICIALIZACIÓN -----
mostrarProductos(productos);
productos.forEach(p => {
    const opt = document.createElement("option");
    opt.value = p.nombre;
    opt.textContent = p.nombre;
    selectPedido.appendChild(opt);
});

// ----- EVENTOS -----
buscador.addEventListener("input", e => {
    const texto = e.target.value.toLowerCase();
    const filtrados = productos.filter(p => p.nombre.toLowerCase().includes(texto));
    mostrarProductos(filtrados);
});

titulo.addEventListener("click", () => mostrarSeccion("productos"));
btnPedido.addEventListener("click", () => mostrarSeccion("pedido"));
btnContacto.addEventListener("click", () => mostrarSeccion("contacto"));

// ----- FORMULARIO DE PEDIDO -----
formPedido.addEventListener("submit", e => {
    e.preventDefault();
    const nombre = document.getElementById("nombre").value;
    const producto = selectPedido.value;
    const cantidad = document.getElementById("cantidad").value;
    const direccion = document.getElementById("direccion").value;

    mensaje.textContent = `✅ Gracias ${nombre}, tu pedido de ${cantidad} ${producto}(s) será enviado a ${direccion}.`;
    mensaje.classList.remove("oculto");
    formPedido.reset();
});
