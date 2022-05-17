const operationLogMapper = new SgData(
    {
        table: 't_operation_log',
        comment: '操作日志表',
        columns: [
            {key: 'gmt_create', type: 'datetime', comment: '日志创建时间'},
            {key: 'app_name', type: 'varchar(255)', comment: '应用名称'},
            {key: 'ip', type: 'varchar(255)', comment: 'IP地址'},
            {key: 'user', type: 'varchar(255)', comment: '用户信息'},
            {key: 'action', type: 'varchar(255)', comment: '用户行为'},
            {key: 'content', type: 'varchar(4000)', comment: '操作内容'}
        ],
    },
    (promise) =>
        new Promise((resolve, reject) => {
            promise
                .then((res) => {
                    console.log(res);
                    resolve(res);
                })
                .catch((err) => {
                    console.error(err);
                    reject(err);
                });
        })
);
let clientIp = null

function traceLog(action, content) {
    if (!clientIp) {
        axios.get('https://api.ipify.org?format=json').then(res => {
            clientIp = res.data.ip
            operationLogMapper.insert({
                'gmt_create': dayjs().format('YYYY-MM-DD HH:mm:ss'),
                'app_name': 'NCHU_TOOLS_APP',
                ip: clientIp,
                user: JSON.stringify(getBrowserInfo()),
                action: action || '',
                content: content || ''
            })
        })
        return
    }
    operationLogMapper.insert({
        'gmt_create': dayjs().format('YYYY-MM-DD HH:mm:ss'),
        'app_name': 'NCHU_TOOLS_APP',
        ip: clientIp,
        user: JSON.stringify(getBrowserInfo()),
        action: action || '',
        content: content || ''
    })
}

function getBrowserInfo() {
    var agent = navigator.userAgent.toLowerCase();
    console.log(agent);
    var arr = [];
    var system = agent.split(' ')[1].split(' ')[0].split('(')[1];
    arr.push(system);
    var regStr_edge = /edge\/[\d.]+/gi;
    var regStr_ie = /trident\/[\d.]+/gi;
    var regStr_ff = /firefox\/[\d.]+/gi;
    var regStr_chrome = /chrome\/[\d.]+/gi;
    var regStr_saf = /safari\/[\d.]+/gi;
    var regStr_opera = /opr\/[\d.]+/gi;
    //IE
    if (agent.indexOf("trident") > 0) {
        arr.push(agent.match(regStr_ie)[0].split('/')[0]);
        arr.push(agent.match(regStr_ie)[0].split('/')[1]);
        return arr;
    }
    //Edge
    if (agent.indexOf('edge') > 0) {
        arr.push(agent.match(regStr_edge)[0].split('/')[0]);
        arr.push(agent.match(regStr_edge)[0].split('/')[1]);
        return arr;
    }
    //firefox
    if (agent.indexOf("firefox") > 0) {
        arr.push(agent.match(regStr_ff)[0].split('/')[0]);
        arr.push(agent.match(regStr_ff)[0].split('/')[1]);
        return arr;
    }
    //Opera
    if (agent.indexOf("opr") > 0) {
        arr.push(agent.match(regStr_opera)[0].split('/')[0]);
        arr.push(agent.match(regStr_opera)[0].split('/')[1]);
        return arr;
    }
    //Safari
    if (agent.indexOf("safari") > 0 && agent.indexOf("chrome") < 0) {
        arr.push(agent.match(regStr_saf)[0].split('/')[0]);
        arr.push(agent.match(regStr_saf)[0].split('/')[1]);
        return arr;
    }
    //Chrome
    if (agent.indexOf("chrome") > 0) {
        arr.push(agent.match(regStr_chrome)[0].split('/')[0]);
        arr.push(agent.match(regStr_chrome)[0].split('/')[1]);
        return arr;
    } else {
        arr.push('请更换主流浏览器，例如chrome,firefox,opera,safari,IE,Edge!')
        return arr;
    }
}
