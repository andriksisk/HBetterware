// ----- RUTA POR DEFECTO PARA IMÁGENES -----
const RUTA_POR_DEFECTO = 'assets/Placeholder.png';

// ----- INVENTARIO (se cargará desde productos.json) -----
let productos = [];

// --- Esperar a que el DOM cargue ---
document.addEventListener('DOMContentLoaded', () => {

    // ----- ELEMENTOS DEL DOM -----
    const contenedorProductos = document.getElementById("productos");
    const buscador = document.getElementById("searchInput");
    if (!buscador) console.warn("Advertencia: #searchInput no encontrado en index.html. El buscador no estará disponible.");
    const selectPedido = document.getElementById("productoPedido");
    const formPedido = document.getElementById("pedidoForm");
    const mensaje = document.getElementById("mensajeConfirmacion");

    const seccionInventario = document.getElementById("inventario");
    const seccionPedido = document.getElementById("pedido");
    const seccionContacto = document.getElementById("contacto");

    const btnPedido = document.getElementById("btnPedido");
    const btnContacto = document.getElementById("btnContacto");
    const titulo = document.getElementById("titulo");

    // --- Referencias del modal y botones ---
    const loginWarning = document.getElementById("loginWarning");
    const modalLoginBtn = document.getElementById("modalLoginBtn");
    const modalCloseBtn = document.getElementById("modalCloseBtn");

    // --- Referencias modal sin existencias ---
    const outOfStockModal = document.getElementById("outOfStockModal");
    const outOfStockMsg = document.getElementById("outOfStockMsg");
    const outOfStockClose = document.getElementById("outOfStockClose");


    // Botón y elementos de registro / perfil
    const btnRegistrar = document.getElementById("btnRegistrar");
    const iconPerfil = document.getElementById("iconPerfil");

    // Sección y elementos del registro/login
    const seccionRegistro = document.getElementById("registro");
    const formRegistro = document.getElementById("formRegistro");
    const registroCampos = document.getElementById("registroCampos");
    const loginCampos = document.getElementById("loginCampos");
    const linkToLogin = document.getElementById("linkToLogin");
    const linkToRegister = document.getElementById("linkToRegister");
    const mensajeRegistro = document.getElementById("mensajeRegistro");
    const btnLogin = document.getElementById("btnLogin");

    // Variables para almacenamiento temporal
    let usuarios = JSON.parse(localStorage.getItem("usuarios")) || [];
    let usuarioActivo = null;

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

    // si lista es null/undefined, evitar errores
    if (!Array.isArray(lista)) return;

    lista.forEach(p => {
        const div = document.createElement("div");
        div.classList.add("producto");

        // imagen (usa campo imagen del producto; si falta usa RUTA_POR_DEFECTO)
        const img = crearImagen(p.imagen, p.producto || p.nombre);

        const info = document.createElement('div');
        info.className = 'producto-info';
        info.innerHTML = `
            <h3>${p.producto || p.nombre || 'Sin nombre'}</h3>
            <p>Código: ${p.codigo ?? ''}</p>
            <p>Cantidad disponible: ${Number(p.cantidad ?? 0)}</p>
            <p class="precio">$${Number(p.precio_estandar ?? p.precio ?? 0).toFixed(2)} MXN</p>
        `;

        const btn = document.createElement('button');
        btn.textContent = 'Añadir';

        // Si el producto no tiene stock, deshabilitar el botón y aplicar estilo
        const cantidadActual = Number(p.cantidad ?? p.cantidadDisponible ?? 0);
        if (cantidadActual <= 0) {
            btn.disabled = true;
            btn.classList.add('btn-disabled');
        } else {
            btn.disabled = false;
            btn.classList.remove('btn-disabled');
        }

        // handler del botón: comprueba stock y sesión y agrega al pedido
        btn.addEventListener('click', () => {
            // protección extra: si sin stock, mostrar modal
            const cantidadNow = Number(p.cantidad ?? p.cantidadDisponible ?? 0);
            if (cantidadNow <= 0) {
                mostrarOutOfStock(p.producto || p.nombre || 'Producto');
                return;
            }
            agregarAlPedido(p);
        });

        div.appendChild(img);
        div.appendChild(info);
        div.appendChild(btn);

        contenedorProductos.appendChild(div);
    });
}


    window.mostrarSeccion = function(nombre) {
        seccionInventario.classList.add("oculto");
        seccionPedido.classList.add("oculto");
        seccionContacto.classList.add("oculto");
        seccionRegistro.classList.add("oculto");

        if (nombre === "inventario") seccionInventario.classList.remove("oculto");
        if (nombre === "pedido") seccionPedido.classList.remove("oculto");
        if (nombre === "contacto") seccionContacto.classList.remove("oculto");
        if (nombre === "registro") seccionRegistro.classList.remove("oculto");
    }

    window.mostrarSeccion = mostrarSeccion;

    function mostrarLoginWarning() {
        if (loginWarning) {
            loginWarning.classList.remove("oculto");
        } else {
            // fallback simple si no existe el modal (seguro)
            alert("Debes iniciar sesión para añadir productos al pedido.");   
        }
    }
    
    function ocultarLoginWarning() {
        if (loginWarning) loginWarning.classList.add("oculto");
    }

    function mostrarOutOfStock(nombreProducto) {
        if (outOfStockMsg) outOfStockMsg.textContent = `Lo sentimos, no quedan existencias de "${nombreProducto}".`;
        if (outOfStockModal) outOfStockModal.classList.remove("oculto");
    }
    
    function ocultarOutOfStock() {
        if (outOfStockModal) outOfStockModal.classList.add("oculto");
    }

    function agregarAlPedido(producto) {
    // Requerir sesión activa para poder añadir al pedido
    if (!usuarioActivo) {
        mostrarLoginWarning();
        return;
    }
    // Si no hay stock, mostrar modal de sin existencias
    if ((producto.cantidad ?? 0) <= 0) {
        mostrarOutOfStock(producto.producto || producto.nombre || 'Producto');
        return;
    }

    // obtener precio unitario (compatibilidad con distintos esquemas)
    const precioUnit = Number(producto.precio_estandar ?? producto.precio ?? 0);

    // Buscar por código si existe, si no por nombre
    const buscarPor = producto.codigo !== undefined ? 'codigo' : 'nombre';
    const valorId = producto.codigo ?? producto.nombre;
    

    let item = pedidoActual.find(p => p.id === valorId);
    if (item) {
        item.cantidadPedida++;
    } else {
        // almacenar datos básicos en memoria (temporal), incluyendo precio unitario
        pedidoActual.push({
            id: valorId,
            nombre: producto.producto || producto.nombre || "Producto",
            cantidadPedida: 1,
            precioUnit: precioUnit
        });
    }

    producto.cantidad--; // disminuir inventario
    mostrarProductos(productos); // actualizar inventario en pantalla
    actualizarTablaPedido(); // actualizar la tabla y total
}


// --- Listener robusto para "Añadir productos" en la sección de pedidos ---
(function initBtnVolverInventario() {
    // Obtener el botón (otra vez, por seguridad)
    const btnVolver = document.getElementById("btnVolverInventario");

    if (!btnVolver) {
        console.warn("btnVolverInventario no encontrado en el DOM. Verifica el id en index.html.");
        return;
    }

    // remover listeners previos (evita múltiple bindings si el script se ejecuta más de una vez)
    btnVolver.replaceWith(btnVolver.cloneNode(true));
    const nuevoBtnVolver = document.getElementById("btnVolverInventario");

    nuevoBtnVolver.addEventListener("click", (e) => {
        e.preventDefault();
        // mostrar inventario y ocultar la sección de pedido
        if (typeof mostrarSeccion === "function") {
            mostrarSeccion("inventario");
        } else {
            // fallback: mostrar/ocultar manualmente
            const seccionInv = document.getElementById("inventario");
            const seccionPed = document.getElementById("pedido");
            if (seccionInv && seccionPed) {
                seccionPed.classList.add("oculto");
                seccionInv.classList.remove("oculto");
            }
        }
    });

    console.log("Listener asignado a #btnVolverInventario");
})();

    function actualizarTablaPedido() {
    if (!cuerpoTabla) return;
    cuerpoTabla.innerHTML = "";

    const resumen = document.getElementById("pedidoResumen");

    // --- Si no hay productos en el pedido ---
    if (!pedidoActual || pedidoActual.length === 0) {
        if (tablaPedido) tablaPedido.classList.add("oculto");
        if (mensajeVacio) mensajeVacio.classList.remove("oculto");
       // Mostrar explícitamente el botón "Añadir productos"
       const btnVolver = document.getElementById("btnVolverInventario");
       if (btnVolver) {
        btnVolver.classList.remove("oculto");
        btnVolver.style.removeProperty('display');
        btnVolver.style.removeProperty('visibility');
        btnVolver.style.removeProperty('opacity');
        // Forzar visibilidad con prioridad solo si es estrictamente necesario:
        try {
            btnVolver.style.setProperty('display', 'inline-block', 'important');
            btnVolver.style.setProperty('visibility', 'visible', 'important');
            btnVolver.style.setProperty('opacity', '1', 'important');
            btnVolver.style.setProperty('pointer-events', 'auto', 'important');
        } catch(e){}
    }

        // OCULTAR TOTAL DEL PEDIDO
        if (resumen) {
            resumen.classList.add("oculto");
            resumen.style.display = "none"; // forzar oculto si alguna regla CSS lo estaba sobrescribiendo
            }

        return;
    }

    // --- Sí hay productos en el pedido ---
    if (tablaPedido) tablaPedido.classList.remove("oculto");
    if (mensajeVacio) mensajeVacio.classList.add("oculto");
    if (btnVolverInventario) btnVolverInventario.classList.add("oculto");

    let total = 0;

    pedidoActual.forEach(item => {
        const producto = productos.find(p => (p.codigo ?? p.nombre) === item.id);

        const precioUnit = Number(
            item.precioUnit ??
            (producto ? (producto.precio_estandar ?? producto.precio ?? 0) : 0)
        );

        const fila = document.createElement("tr");

        fila.innerHTML = `
            <td>${item.id ?? ''}</td>
            <td>${item.nombre}</td>
            <td>${item.cantidadPedida}</td>
            <td>$${precioUnit.toFixed(2)} MXN</td>
            <td class="td-actions"><button class="btn-eliminar" data-id="${item.id}" type="button" aria-label="Eliminar ${item.nombre}">Eliminar</button></td>
        `;
        cuerpoTabla.appendChild(fila);

        const btnEliminar = fila.querySelector('.btn-eliminar');
        if (btnEliminar) {
            btnEliminar.addEventListener('click', () => {
                const id = btnEliminar.getAttribute('data-id');
                eliminarDelPedido(id);
            });
        }

        total += precioUnit * item.cantidadPedida;
    });

    // Mostrar total
    const totalNodo = document.getElementById("totalPedido");
    if (totalNodo) totalNodo.textContent = `$${total.toFixed(2)} MXN`;

    // ✔️ MOSTRAR TOTAL DEL PEDIDO
    if (resumen) {
    resumen.classList.remove("oculto");
    resumen.style.display = ""; // quitar el display forcado para dejar que CSS maneje la visibilidad
    }

}

function eliminarDelPedido(id) {
    if (!id) return;

    // encontrar índice en pedidoActual
    const idx = pedidoActual.findIndex(it => it.id === id);
    if (idx === -1) return;

    const item = pedidoActual[idx];
    const cantidadADevolver = Number(item.cantidadPedida ?? 1);

    // devolver stock en el inventario (buscar producto por id)
    const productoEnInventario = productos.find(p => (p.codigo ?? p.nombre) === id);
    if (productoEnInventario) {
        // sumar la cantidad devuelta
        productoEnInventario.cantidad = Number(productoEnInventario.cantidad ?? 0) + cantidadADevolver;
    }

    if (item.cantidadPedida > 1) {
    item.cantidadPedida--;
} else {
    pedidoActual.splice(idx, 1);
}


    // re-renderizar inventario y tabla
    mostrarProductos(productos);
    actualizarTablaPedido();
}


    // ----- CARGAR PRODUCTOS DESDE JSON -----
    async function cargarProductos() {
        try {
            const res = await fetch("productos.json");
            productos = await res.json();
            mostrarProductos(productos);

            // llenar select de pedido si existe
            if (selectPedido) {
                selectPedido.innerHTML = "";
                productos.forEach(p => {
                    const opt = document.createElement("option");
                    opt.value = p.codigo;
                    opt.textContent = p.producto;
                    selectPedido.appendChild(opt);
                });
            }
        } catch (error) {
            console.error("❌ Error cargando productos.json:", error);
        }
    }

    // ----- EVENTOS -----
    if (buscador) {
        buscador.addEventListener("input", e => {
            const texto = e.target.value.toLowerCase();
            const filtrados = productos.filter(p => p.producto.toLowerCase().includes(texto));
            mostrarProductos(filtrados);
        });
    }

    if (titulo) titulo.addEventListener("click", () => mostrarSeccion("inventario"));
    if (btnPedido) btnPedido.addEventListener("click", () => { actualizarTablaPedido(); mostrarSeccion("pedido"); });
    if (btnContacto) btnContacto.addEventListener("click", () => mostrarSeccion("contacto"));
    // === MOSTRAR SECCIÓN DE REGISTRO ===
    if (btnRegistrar) {
        btnRegistrar.addEventListener("click", () => {
            // mostrar sección registro y forzar vista de registro (no login)
            mostrarSeccion("registro");
            registroCampos.classList.remove("oculto");
            loginCampos.classList.add("oculto");
            mensajeRegistro.textContent = "";
        });
    }

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


    // Si el usuario desea iniciar sesión desde el modal
    if (modalLoginBtn) {
        modalLoginBtn.addEventListener("click", () => {
            ocultarLoginWarning();      
            // mostrar la sección de registro/login en modo login
            mostrarSeccion("registro");
            // forzar vista de login
            if (registroCampos && loginCampos) {
                registroCampos.classList.add("oculto");
                loginCampos.classList.remove("oculto");
            }
        });
    }

    if (outOfStockClose) {
        outOfStockClose.addEventListener("click", () => {
            ocultarOutOfStock();
        });
    }
    
    // cerrar modal si se hace click fuera del contenido
    if (outOfStockModal) {
        outOfStockModal.addEventListener("click", (e) => {
            if (e.target === outOfStockModal) ocultarOutOfStock();
        });
    }

    // Cerrar modal
    if (modalCloseBtn) {
        modalCloseBtn.addEventListener("click", () => {
            ocultarLoginWarning();
        });
    }
    
    // Cerrar modal si se hace click fuera del contenido
    if (loginWarning) {
        loginWarning.addEventListener("click", (e) => {
            if (e.target === loginWarning) ocultarLoginWarning();
        });
    }

    // ==== CAMBIO ENTRE REGISTRO E INICIO DE SESIÓN ====
    // --- Enlace: ir a Login desde Registro ---
if (linkToLogin) {
    linkToLogin.addEventListener("click", (e) => {
        e.preventDefault();
        registroCampos.classList.add("oculto");
        loginCampos.classList.remove("oculto");
        mensajeRegistro.textContent = "";
    });
}

// --- Enlace: volver a Registro desde Login ---
if (linkToRegister) {
    linkToRegister.addEventListener("click", (e) => {
        e.preventDefault();
        loginCampos.classList.add("oculto");
        registroCampos.classList.remove("oculto");
        mensajeRegistro.textContent = "";
    });
}

// --- Registro por formulario (ya tenías similar): evitar que muestre login automáticamente ---
if (formRegistro) {
    formRegistro.addEventListener("submit", (e) => {
        // Este handler solo gestiona el registro (si la vista actual es registro)
        // evita que el form haga submit en la vista de login (login usa btnLogin)
        if (!registroCampos.classList.contains("oculto")) {
            e.preventDefault();
            // ... aquí va tu lógica de registro existente (guardar en usuarios/localStorage) ...
            // Ejemplo resumido:
            const nombre = document.getElementById("nombreUsuario").value.trim();
            const telefono = document.getElementById("telefonoUsuario").value.trim();
            const direccion = document.getElementById("direccionUsuario").value.trim();
            const localidad = document.getElementById("localidadUsuario").value.trim();
            const pass = document.getElementById("passUsuario").value.trim();

            if (usuarios.find(u => u.nombre === nombre)) {
                mensajeRegistro.textContent = "⚠️ Este nombre de usuario ya está registrado.";
                mensajeRegistro.style.color = "red";
                return;
            }

            const nuevoUsuario = { nombre, telefono, direccion, localidad, pass, rol: "cliente" };
            usuarios.push(nuevoUsuario);
            localStorage.setItem("usuarios", JSON.stringify(usuarios));

            mensajeRegistro.textContent = "✅ Registro exitoso. Ya puedes iniciar sesión.";
            mensajeRegistro.style.color = "green";
            formRegistro.reset();

            // opcional: cambiar a login automáticamente
            registroCampos.classList.add("oculto");
            loginCampos.classList.remove("oculto");
        }
    });
}

// ==== INICIAR SESIÓN ====
// --- Login: botón separado (no submit del form) ---
if (btnLogin) {
    btnLogin.addEventListener("click", () => {
        const nombre = document.getElementById("loginNombre").value.trim();
        const pass = document.getElementById("loginPass").value.trim();

        // verificar admin fijo
        if (nombre === "Rosa" && pass === "99") {
            usuarioActivo = { nombre: "Rosa", rol: "admin" };
            mostrarPerfil();
            return;
        }

        const usuario = usuarios.find(u => u.nombre === nombre && u.pass === pass);
        if (!usuario) {
            mensajeRegistro.textContent = "❌ Credenciales incorrectas.";
            mensajeRegistro.style.color = "red";
            return;
        }

        usuarioActivo = usuario;
        mostrarPerfil();
    });
}

// ==== FUNCIÓN PARA MOSTRAR PERFIL ====
function mostrarPerfil() {
    btnRegistrar.classList.add("oculto");
    iconPerfil.classList.remove("oculto");

    if (usuarioActivo.rol === "admin") {
        iconPerfil.classList.add("perfil-admin");
    } else {
        iconPerfil.classList.remove("perfil-admin");
    }

    mostrarSeccion("inventario");
    mensajeRegistro.textContent = "";
}


    // 🚀 Inicializar cargando productos desde JSON
    actualizarTablaPedido();
    cargarProductos();
});

window.addEventListener("beforeunload", () => {
  navigator.sendBeacon("/cerrar");
});
