let MAX = 15000;

for( index = 1; index <= MAX; index++ ){

    const grupos = {
        1000: 'A',
        10000: 'B',
        25000: 'C',
        50000: 'D',
        100000: 'E'
    };

    let obj = {
        /**
         * O atributo _id foi substituido pelo index para efeito de facilidade
         * no uso do teste. Em situacoes reais, o ObjectID gerado pelo MonboDB
         * tem uma serie de vantagens, e é quase que obrigatório em situações
         * de particionamento.
         */
        "_id": "" + index,
        // -------------------------------------------------------------------

        "name": "Restaurante " + index,
        "endereco": "Rua " + index + ", no. " + index,
        "grupo": grupos[Object.keys(grupos)
            .reduce((prev, act) => {
                let cond = (index > prev && index <= act);
                let ret;
                cond && (() => {
                    ret = act
                })() || !cond && (() => {
                    ret = prev
                })();
                return ret;
            }, 0)]
    };

    let rest_insert = db.restaurante_info.insertOne(obj, {w: 1});
    if( rest_insert.insertedId % 5000 === 0 ){
        print(rest_insert.insertedId + " incluidos");
    }

    if( rest_insert.acknowledged ) {
        let restaurantOid = rest_insert.insertedId;
        let now = new Date();

        let createdEvent = {
            name: 'OFFLINE',
            timestamp: now.getTime()
        };

        let eventos = [createdEvent];

        let disponibilidade = {
            restaurante_id: restaurantOid,
            data: now,
            tempo_online: 0,
            tempo_offline: 0,
            eventos: eventos
        };

        db.disponibilidade.insertOne(disponibilidade);

        let realtime_obj = {
            restaurante_id: restaurantOid,
            grupo: obj.grupo,
            state: 'OFFLINE',
            last_updated: now.getTime()
        };

        db.realtime_state.insertOne(realtime_obj);
    }

}

print(MAX + " incluidos");

