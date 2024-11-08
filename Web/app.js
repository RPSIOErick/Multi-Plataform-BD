const { db, app, firebaseAuth } = require('./config/config');
const express = require('express');
const session = require('express-session');
const bodyParser = require('body-parser');
const { signInWithEmailAndPassword, createUserWithEmailAndPassword, updateProfile } = require("firebase/auth");

// Middleware para autenticação
function isAuthenticated(req, res, next) {
    if (req.session.user) {
        return next();
    }
    res.redirect("/login");
}

// Configurações de middlewares
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());
app.use(session({
    secret: "gertrudes",
    resave: false,
    saveUninitialized: true,
}));

// Rota padrão para redirecionar conforme a autenticação do usuário
app.get('/', (req, res) => {
    if (req.session.user) {
        res.redirect('/home');
    } else {
        res.redirect('/login');
    }
});

// Rotas de renderização
app.get('/login', (req, res) => {
    res.render('login');
});

app.get('/registrar', (req, res) => {
    res.render('signup');
});

app.get('/home', isAuthenticated, (req, res) => {
    res.render('home', { user: req.session.user });
});

app.get('/criar', isAuthenticated, (req, res) => {
    res.render('criar');
});

app.get('/lista', isAuthenticated, async (req, res) => {
    const statusFilter = req.query.status || '';
    let query = db.collection('servicos').get();

    if (statusFilter) {
        query = db.collection('servicos').where('status', '==', statusFilter).get();
    }

    const dataSnapshot = await query;
    const servicos = [];

    dataSnapshot.forEach((doc) => {
        servicos.push({
            id: doc.id,
            cli_name: doc.get('client'),
            initial_date: doc.get('i_date'),
            final_date: doc.get('f_date'),
            price: doc.get('price'),
            service_description: doc.get('description'),
            status: doc.get('status')
        });
    });

    res.render('lista', { servicos, statusFilter });
});

// Rotas de autenticação
app.post('/login', async (req, res) => {
    const { email, password } = req.body;

    try {
        const userCredential = await signInWithEmailAndPassword(firebaseAuth, email, password);
        const user = userCredential.user;
        
        req.session.user = {
            uid: user.uid,
            name: user.displayName,
            email: user.email  // Adicionando o e-mail
        };
        
        
        res.redirect('/home');
    } catch (err) {
        res.render('login', { error: err.message });
    }
});

app.post('/signup', async (req, res) => {
    const { name, email, password } = req.body;

    try {
        const userCredential = await createUserWithEmailAndPassword(firebaseAuth, email, password);
        const user = userCredential.user;

        await updateProfile(user, {
            displayName: name
        });

        req.session.user = {
            uid: user.uid,
            name: user.displayName
        };

        res.redirect('/home');
    } catch (err) {
        res.render('signup', { error: err.message });
    }
});

// Rota para exibir o serviço por ID
app.get('/servico/:id', isAuthenticated, async (req, res) => {
    const { id } = req.params;

    const doc = await db.collection('servicos').doc(id).get();

    if (!doc.exists) {
        return res.status(404).json({ success: false, message: "Serviço não encontrado." });
    }

    const servico = {
        id: doc.id,
        cli_name: doc.get('client'),
        initial_date: doc.get('i_date'),
        final_date: doc.get('f_date'),
        price: doc.get('price'),
        service_description: doc.get('description'),
        status: doc.get('status')
    };

    res.render('editar', { servico });
});

// Rota para atualizar o serviço
app.put('/servico/:id', isAuthenticated, async (req, res) => {
    const { id } = req.params;
    const { cli_name, initial_date, final_date, price, service_description, status } = req.body;

    if (!cli_name) {
        return res.status(400).json({ success: false, message: "Client name is required." });
    }

    try {
        await db.collection('servicos').doc(id).update({
            client: cli_name,
            i_date: initial_date,
            f_date: final_date,
            price: price,
            description: service_description,
            status: status
        });

        res.status(200).json({ success: true, message: "Serviço atualizado com sucesso!" });
    } catch (error) {
        console.error("Erro ao atualizar serviço:", error);
        res.status(500).json({ success: false, message: "Erro ao atualizar serviço. Tente novamente." });
    }
});

// Rota para excluir um serviço
app.delete('/servico/deletar/:id', isAuthenticated, async (req, res) => {
    const { id } = req.params;

    try {
        const doc = await db.collection('servicos').doc(id).get();

        if (!doc.exists) {
            return res.status(404).json({ success: false, message: "Serviço não encontrado." });
        }

        await db.collection('servicos').doc(id).delete();
        res.json({ success: true, message: "Serviço excluído com sucesso!" });
    } catch (error) {
        console.error("Erro ao excluir o serviço:", error);
        res.status(500).json({ success: false, message: "Erro ao excluir o serviço." });
    }
});

// Rota para criar um serviço
app.post('/servico/criar', isAuthenticated, async (req, res) => {
    const { cli_name, initial_date, final_date, price, service_description, status } = req.body;

    if (!cli_name) {
        return res.status(400).json({ success: false, message: "Client name is required." });
    }

    try {
        await db.collection('servicos').add({
            client: cli_name,
            i_date: initial_date,
            f_date: final_date,
            price: price,
            description: service_description,
            status: status
        });

        res.status(200).json({ success: true, message: "Serviço criado com sucesso!" });
    } catch (error) {
        console.error("Erro ao criar serviço:", error);
        res.status(500).json({ success: false, message: "Erro ao criar serviço. Tente novamente." });
    }
});

app.get('/minha-conta', isAuthenticated, (req, res) => {
    res.render('minha-conta', { user: req.session.user });
});

app.post('/minha-conta', isAuthenticated, async (req, res) => {
    const { name, email, password } = req.body;
    const user = firebaseAuth.currentUser;  // Obtendo o usuário autenticado atual

    try {
        if (name) {
            await updateProfile(user, { displayName: name });
            req.session.user.name = name; // Atualiza o nome na sessão
        }

        if (email && email !== user.email) {
            await user.updateEmail(email);  // Atualiza o e-mail
            req.session.user.email = email; // Atualiza o e-mail na sessão
        }

        if (password) {
            await user.updatePassword(password);  // Atualiza a senha
        }

        res.render('minha-conta', { user: req.session.user, success: "Dados atualizados com sucesso!" });
    } catch (error) {
        res.render('minha-conta', { user: req.session.user, error: error.message });
    }
});


const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});
