let MAX = 1002;

Array.apply(0,Array(MAX)).map( (v,i) => { return i+1; }).forEach( index => {

    const grupos = {
        1000: 'A',
        10000: 'B',
        25000: 'C',
        50000: 'D',
        100000: 'E'
    };

    let obj = {
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
    print(obj.name + ", " + obj.grupo + " -> " + rest_insert.acknowledged);

    rest_insert.acknowledged && (() => {
    	let restaurantOid = rest_insert.insertedId;
        let now = new Date();
        now.setHours(3,0,0);

        let createdEvent = {
            name: 'CREATED',
            timestamp: now.getTime()
        };

        let eventos = [createdEvent];

        let disponibilidade = {
            restaurante_id: restaurantOid,
            data: now,
            tempo_total_offline: 0,
            tempo_total_indisponivel: 0,
            eventos: eventos
        };

        db.disponibilidade.insertOne(disponibilidade);
        db.realtime_state.insertOne({restaurante_id: restaurantOid, state: 'CREATED', last_updated: now.getTime()});
    })();
});
