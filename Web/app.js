const { db, app } = require('./config/config')

const PORT = process.env.PORT || 3000

app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`)
})

app.get('/', (req, res) => {
    res.render('home')
})

app.get('/lista', async (req, res) => {
    const statusFilter = req.query.status || ''; // Se não houver filtro, pega todos os serviços
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

app.get('/servico/:id', async (req, res) => {
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
})

app.put('/servico/:id', async (req, res) => {

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

})

app.delete('/servico/deletar/:id', async (req, res) => {
    const { id } = req.params;  // Recebendo o ID pela URL

    try {
        // Verifique se o serviço existe
        const doc = await db.collection('servicos').doc(id).get();

        if (!doc.exists) {
            return res.status(404).json({ success: false, message: "Serviço não encontrado." });
        }

        // Deletando o serviço
        await db.collection('servicos').doc(id).delete();

        // Retorno de sucesso
        res.json({ success: true, message: "Serviço excluído com sucesso!" });
    } catch (error) {
        console.error("Erro ao excluir o serviço:", error);
        res.status(500).json({ success: false, message: "Erro ao excluir o serviço." });
    }
});


app.get('/criar', (req, res) => {
    res.render('criar')
})

app.post('/servico/criar', async (req, res) => {
    console.log(req.body); // Verifique o corpo da requisição

    const { cli_name, initial_date, final_date, price, service_description, status } = req.body;

    if (!cli_name) {
        return res.status(400).json({ success: false, message: "Client name is required." });
    }

    try {
        const store = await db.collection('servicos').add({
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
