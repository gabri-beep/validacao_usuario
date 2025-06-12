document.addEventListener("DOMContentLoaded", () => {
    const API_BASE = "http://localhost:8080/usuario";

    // Utilitário simples de validação
    function validarUsuario(usuario) {
        const camposObrigatorios = ["username", "password", "nome", "sobrenome", "email"];
        for (const campo of camposObrigatorios) {
            if (!usuario[campo] || usuario[campo].trim() === "") {
                alert(`Campo obrigatório "${campo}" está vazio.`);
                return false;
            }
        }
        return true;
    }

    // -------- CRIAR USUÁRIO --------
    const createForm = document.getElementById("create-user-form");
    createForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const form = e.target;
        const usuarioDto = {
            username: form.username.value,
            password: form.password.value,
            nome: form.nome.value,
            sobrenome: form.sobrenome.value,
            email: form.email.value,
        };

        if (!validarUsuario(usuarioDto)) return;

        const formData = new FormData();
        formData.append("usuarioDto", new Blob([JSON.stringify(usuarioDto)], { type: "application/json" }));

        try {
            const response = await fetch(API_BASE, {
                method: "POST",
                body: formData,
            });

            if (!response.ok) throw new Error("Erro ao criar usuário");

            const data = await response.json();
            alert("Usuário criado com sucesso!");
            console.log(data);
            form.reset();
            carregarUsuarios();
        } catch (err) {
            alert("Erro ao criar usuário: " + err.message);
        }
    });

    // -------- LISTAR USUÁRIOS --------
    async function carregarUsuarios() {
        try {
            const response = await fetch(API_BASE);
            const data = await response.json();

            const lista = document.getElementById("user-list");
            lista.innerHTML = "";

            data.forEach((user) => {
                const li = document.createElement("li");
                li.textContent = `ID: ${user.id} | ${user.nome} ${user.sobrenome} | ${user.username} | ${user.email}`;
                lista.appendChild(li);
            });
        } catch (err) {
            console.error("Erro ao carregar usuários:", err);
        }
    }

    // -------- ATUALIZAR USUÁRIO --------
    const updateForm = document.getElementById("update-user-form");
    updateForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const form = e.target;
        const id = form.id.value;

        const usuarioDto = {
            username: form.username.value,
            password: form.password.value,
            nome: form.nome.value,
            sobrenome: form.sobrenome.value,
            email: form.email.value,
        };

        if (!validarUsuario(usuarioDto)) return;

        try {
            const response = await fetch(`${API_BASE}/${id}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(usuarioDto),
            });

            if (!response.ok) throw new Error("Erro ao atualizar usuário");

            alert("Usuário atualizado com sucesso!");
            form.reset();
            carregarUsuarios();
        } catch (err) {
            alert("Erro ao atualizar usuário: " + err.message);
        }
    });

    // -------- DELETAR USUÁRIO --------
    const deleteForm = document.getElementById("delete-user-form");
    deleteForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const id = e.target.id.value;

        if (!id) {
            alert("Informe o ID do usuário");
            return;
        }

        try {
            const response = await fetch(`${API_BASE}/${id}`, {
                method: "DELETE",
            });

            if (response.status === 204) {
                alert("Usuário deletado com sucesso!");
                carregarUsuarios();
            } else {
                alert("Usuário não encontrado");
            }
        } catch (err) {
            alert("Erro ao deletar usuário: " + err.message);
        }
    });

    // -------- LOGIN --------
    const loginForm = document.getElementById("login-form");
    loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const form = e.target;
        const loginDto = {
            username: form.username.value,
            password: form.password.value,
        };

        if (!loginDto.username || !loginDto.password) {
            alert("Preencha os campos de login.");
            return;
        }

        try {
            const response = await fetch(`${API_BASE}/login`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Accept-Language": "pt-BR"
                },
                body: JSON.stringify(loginDto),
            });

            const data = await response.json();

            if (response.ok) {
                alert("Login realizado: " + data.message);
            } else {
                alert("Falha no login: " + data.message);
            }
        } catch (err) {
            alert("Erro ao fazer login: " + err.message);
        }
    });

    // Carrega lista ao iniciar
    carregarUsuarios();
});
