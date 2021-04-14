let $ = a=>document.querySelector(a)

let map
let heatmapOverlay

let vue = new Vue({
    el: '#app',
    data: {
        pos: {
            lng: null,
            lat: null
        },
        storeInfo: null,
        storeId: null,
        htmlData: {
            topNavList: [
                '关于',
                '产品',
                '数据',
                '服务',
                '联系'
            ],
            banner: [
                {
                    id: 1,
                    imgSrc: './pictures/store-banner01.jpg',
                    title: '一键化定位',
                    details: ['经纬度']
                },
                {
                    id: 2,
                    imgSrc: './pictures/store-banner02.jpg',
                    title: '智能分析',
                    details: [
                        '搜寻最近定位地址',
                        '计算最短行程',
                        '分析实用性'
                    ]
                },
                {
                    id: 3,
                    imgSrc: './pictures/store-banner03.png',
                    title: '功能性查询',
                    details: ['周边, 物业主信息, 电话, 信息地址']
                }
            ],
            mapUtils: [
                {
                    img: './pictures/core-gongjiao.png',
                    text: '交通站点',
                    type: 'gongjiao',
                    isShow: true,
                    number: 0,
                },
                {
                    img: './pictures/core-xuexiao.png',
                    text: '学校',
                    type: 'xuexiao',
                    isShow: true,
                    number: 0,
                },
                {
                    img: './pictures/core-xiezilou.png',
                    text: '写字楼',
                    type: 'xiezilou',
                    isShow: true,
                    number: 0,
                },
                {
                    img: './pictures/core-home.png',
                    text: '住宿区',
                    type: 'home',
                    isShow: true,
                    number: 0,
                },
                {
                    img: './pictures/core-shopping.png',
                    text: '大型商场',
                    type: 'shopping',
                    isShow: true,
                    number: 0,
                },
                {
                    img: './pictures/core-canyin.png',
                    text: '餐饮',
                    type: 'canyin',
                    isShow: true,
                    number: 0,
                },
                {
                    img: './pictures/core-happy.png',
                    text: '休闲娱乐',
                    type: 'happy',
                    isShow: true,
                    number: 0,
                },
                {
                    img: './pictures/core-bianlidian.png',
                    text: '便利店',
                    type: 'bianlidian',
                    isShow: true,
                    number: 0,
                }
            ],
            hotMapIsShow: true,
            routeIsShow: true,
        },
        cssData: {
            windowWidth: null,
        },
        homesInfo: null,
    },
    methods: {
        getQueryString(name) {
            let reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
            let r = window.location.search.substr(1).match(reg);
            if (r != null) {
                return unescape(r[2]);
            }
                return null;
        },
        // 初始化, 在地图上标出店铺的精准地标 和 路线 的函数
        addMarker(lng_lat, type, distance=0) { // lng_lat => '166.00,166.00'
            if (!map) {
                alert('BMap丢失')
                return
            }
            
            let lng = lng_lat.split(',')[0] * 1 // 经度
            let lat = lng_lat.split(',')[1] * 1 // 经度

            // 转化type
            if(type === 'convenience') type = 'bianlidian'
            else if(type === 'food') type = 'canyin'
            else if(type === 'traffic') type = 'gongjiao'
            else if(type === 'shopping') type = 'shopping'
            else if(type === 'office') type = 'xiezilou'
            else if(type === 'relaxation') type = 'happy'
            else if(type === 'school') type = 'xuexiao'
            else if(type === 'house') {
                type = 'home'

                // 初始化路线
                this.drawRoute(lng, lat, distance)
            }

            this.htmlData.mapUtils.forEach( (item,i) => {
                if(item.type === type) {
                    this.htmlData.mapUtils[i].number += 1
                }
            })

            console.log('res: ', lng, lat, type)

            let point = new BMap.Point(lng, lat)
            let myIcon = new BMap.Icon(`./pictures/core-${type}.png`, new BMap.Size(32, 32))
            // 指定位置及标注的图标
            let marker = new BMap.Marker(point, { icon: myIcon })  // 创建标注
            if(type){
                let label = new BMap.Label(type, { offset: new BMap.Size(32, -16) })
                marker.setLabel(label)
            }

            // 将不同类型的marker分开储存起来
            if(type === 'bianlidian') {
                this.bianlidian = (this.bianlidian === null || this.bianlidian === undefined) ? [] : this.bianlidian
                this.bianlidian.push(marker)
            }
            else if(type === 'canyin') {
                this.canyin = (this.canyin === null || this.canyin === undefined) ? [] : this.canyin
                this.canyin.push(marker)
            }
            else if(type === 'gongjiao') {
                this.gongjiao = (this.gongjiao === null || this.gongjiao === undefined) ? [] : this.gongjiao
                this.gongjiao.push(marker)
            }
            else if(type === 'shopping') {
                this.shopping = (this.shopping === null || this.shopping === undefined) ? [] : this.shopping
                this.shopping.push(marker)
            }
            else if(type === 'xiezilou') {
                this.xiezilou = (this.xiezilou === null || this.xiezilou === undefined) ? [] : this.xiezilou
                this.xiezilou.push(marker)
            }
            else if(type === 'happy') {
                this.happy = (this.happy === null || this.happy === undefined) ? [] : this.happy
                this.happy.push(marker)
            }
            else if(type === 'xuexiao') {
                this.xuexiao = (this.xuexiao === null || this.xuexiao === undefined) ? [] : this.xuexiao
                this.xuexiao.push(marker)
            }
            else if(type === 'home') {
                this.home = (this.home === null || this.home === undefined) ? [] : this.home
                this.home.push(marker)
            }
            // 添加到地图上
            map.addOverlay(marker)
        },
        hideMarker(type) {
            if(!this[type]) return
            this[type].forEach(item => {
                if(type === 'home') {
                    this.polylines.forEach(polyline => {
                        polyline.hide()
                    })
                }
                item.hide()
            })
        },
        showMarker(type) {
            if(!this[type]) return
            // console.log('show ... ', type)
            this[type].forEach( marker => {
                if(type === 'home') {
                    this.polylines.forEach(polyline => {
                        polyline.show()
                    })
                }
                marker.show()
            })
        },
        toggleShowMarker(type) {
            this.htmlData.mapUtils.forEach( (item,i) => {
                if(item.type === type) {
                    if(item.isShow) {
                        this.hideMarker(type)
                        this.htmlData.mapUtils[i].isShow = false
                    }
                    else{
                        this.showMarker(type)
                        this.htmlData.mapUtils[i].isShow = true
                    }
                }
            })
        },
        // 路线规划
        drawRoute(lng,lat,distance) {
            let polyline = new BMap.Polyline(
                [
                    new BMap.Point(this.pos.lng, this.pos.lat),
                    new BMap.Point(lng, lat)
                ], 
                {
                    strokeColor: "blue",
                    strokeWeight: 6,
                    strokeOpacity: 0.5
                }
            )

            if(this.polylines === null || this.polylines === undefined) 
                this.polylines = []

            this.polylines.push(polyline)
            
            map.addOverlay(polyline);
        },
        hideRoute() {
            this.polylines.forEach(polyline => {
                polyline.hide()
            })
        },
        showRoute() {
            this.polylines.forEach(polyline => {
                polyline.show()
            })
        },
        routeToggle() {
            if(this.htmlData.routeIsShow) {
                this.hideRoute()
            }
            else{
                this.showRoute()
            }
            this.htmlData.routeIsShow = !this.htmlData.routeIsShow
        },
        hotMapToggle() {
            heatmapOverlay.toggle()
            this.htmlData.hotMapIsShow = !this.htmlData.hotMapIsShow
        },
        // 返回到当前店铺的坐标
        reGetCurrentStore() {
            let point = new BMap.Point(this.pos.lng,this.pos.lat)
            map.centerAndZoom(point, 19);
        },
        gotoHome({lng,lat}) {
            let point = new BMap.Point(lng,lat)
            map.centerAndZoom(point, 25)
        },
    },
    mounted() {
        setTimeout(() => {

            // 获取所有要用到的数据
            this.storeInfo = JSON.parse(localStorage.getItem(this.getQueryString('id')))
            
            // 判断用户是不是直接通过不带参数的url访问当前页面的, 如果是,就提示用户不能这样
            this.storeId = this.getQueryString('id')
            if(this.storeId === null || this.storeInfo === null) {
                alert('该网页无法直接访问哦(因为缺少storeId参数)')
            }

            // 创建百度地图
            let lng = this.storeInfo.store.location.split(',')[0] * 1 // 经度
            let lat = this.storeInfo.store.location.split(',')[1] * 1 // 经度
            this.pos ={
                lng,
                lat
            }
            newMap(lng,lat)

            // 画出当前店铺的地标
            let point = new BMap.Point(lng, lat)
            let myIcon = new BMap.Icon(`./pictures/mifeng64.png`, new BMap.Size(64, 64))
            // 指定位置及标注的图标
            let marker = new BMap.Marker(point, { icon: myIcon })  // 创建标注
            let label = new BMap.Label('当前店铺', { offset: new BMap.Size(32, -16) })
            marker.setLabel(label)
            map.addOverlay(marker)

            // 响应式布局所依赖的代码
            this.cssData.windowWidth = window.innerWidth
            window.onresize = () => {
                this.cssData.windowWidth = window.innerWidth
            }
            
            // 人口热力图
            apiUtils.getPopulationHotMapData(this.storeId)
            .then(hotMapArr => {
                heatmapOverlay.setDataSet({data: hotMapArr,max:100000})
            })

            // 展示百度地图的所有图标
            fetch(BASE_URL+`/draw/surrounds/${this.storeId}`)
            .then(res => res.json())
            .then(res => {
                this.homesInfo = []
                res.data.forEach(item => {
                    this.addMarker(item.location,item.type, item.distance)
                    if(item.type === 'house') {
                        this.homesInfo.push({
                            lng: item.location.split(',')[0] * 1,
                            lat: item.location.split(',')[1] * 1,
                            distance: item.distance * 1
                        })
                    }
                })
            })

        }, 20)
    },
})

let footerVue = new Vue({
    el: '#footer',
    data: {
        htmlData: {
            footer: [
                {
                    id: 0,
                    top: '关于我们',
                    items: [
                        '蜜蜂简介',
                        '企业文化',
                        '荣誉资质',
                        '社会责任',
                        '创意设计',
                        '蜜蜂展翅'
                    ]
                },
                {
                    id: 1,
                    top: '新闻动态',
                    items: [
                        '实时新闻',
                        '周边活动',
                        '媒体报道',
                        '新闻中心',
                        '品牌活动',
                        '合作伙伴'
                    ]
                },
                {
                    id: 2,
                    top: '快速链接',
                    items: [
                        '产品中心',
                        '服务中心',
                        '视频中心',
                        '经典案例',
                        '行业方案',
                        '联系我们'
                    ]
                }
            ]
        },
        cssData: {
            windowWidth: null,
        }
    },
    mounted() {
        setTimeout(() => {
            // 响应式布局所依赖的代码
            this.cssData.windowWidth = window.innerWidth
            window.onresize = () => {
                this.cssData.windowWidth = window.innerWidth
            }
        },20)
    },
})

// 曲线图
{
    // echarts.init($('#echarts1')).setOption( {
    //         legend: {
    //             x: 'left',
    //             y: 20,
    //             data: [
    //                 {
    //                     name:'SALES',
    //                     icon: 'circle',
    //                     textStyle: {
    //                         color: 'red',
    //                     }
    //                 },
    //                 {
    //                     name:'VISITS',
    //                 },
    //                 {
    //                     name:'CLICKS',
    //                 }]
    //         },
    //         xAxis: {
    //             data: ["2018-01-01","2018-01-02","2018-01-03","2018-01-04","2018-01-05","2018-01-06"]
    //         },
    //         yAxis: {
    //             // data: [0,500,1000,1500,2000]
    //         },
    //         series: [{
    //             name: 'SALES',
    //             type: 'line',
    //             smooth: true,
    //             symbol: 'circle', // 拐点设置为实心
    //             symbolSize: 10, // 拐点大小
    //             itemStyle: { // 拐点
    //                 normal: {
    //                     color: 'green',
    //                     // borderColor: 'black'
    //                 }
    //             },
    //             lineStyle: { // 曲线
    //                 normal: {
    //                     color: 'green',
    //                     width: '2'
    //                 },
    //             },
    //             areaStyle: {
    //                 normal: {
    //                     color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
    //                         // 4个参数用于配置渐变色的起止位置, 这4个参数依次对应右/下/左/上四个方位. 而0 0 0 1则代表渐变色从正上方开始
    //                         offset: 0,
    //                         color: 'rgb(58,219,27)'
    //                     }, 
    //                     {
    //                         offset: .2,
    //                         color: 'rgb(92,217,61)'
    //                     },
    //                     {
    //                         offset: .5,
    //                         color: 'rgb(184,238,169)'
    //                     },
    //                     {
    //                         offset: 1,
    //                         color: 'rgb(255,255,255)'
    //                     }])
    //                 }
    //             },
    //             data: [5, 20, 36, 10, 10, 20]
    //         }]
    // } )
}

// 柱状图
{   
    function drawEcharts1(avgRent,rent) {
        echarts.init($('#echarts1')).setOption({
            title: {
                text: '租金对比(单位: /日/㎡)'
            },
            toolbox: {
                // show: true,
                feature: {
                    saveAsImage:{
                        show:true
                    },
                    restore:{
                        show:true
                    },
                    dataView:{
                        show:true
                    },
                    dataZoom:{
                        show:true
                    }
                }
            },
            tooltip:{
                trigger:'axis'
            },
            xAxis: {
                data: ["平均租金", "当前店铺的租金"]
            },
            yAxis: {},
            series: [{
                type: 'bar',
                data: [avgRent, rent],
            }]
        })
    }
    setTimeout(() => {
        // 虚构
        drawEcharts1(45,32)

        fetch(BASE_URL+`/draw/housePrice/${vue.storeId}`,{
            mode: 'cors'
        })
        .then(res => res.json())
        .then(res => {
            const avgRent = res.data.avgRent
            const rent = res.data.rent
            drawEcharts1(avgRent,rent)
        })
    }, 200);
}

// 饼图
{
    // echarts.init($('#echarts2')).setOption({
    //     series : [
    //         {
    //             name: '访问来源',
    //             type: 'pie',
    //             radius: '55%',
    //             data:[
    //                 {value:235, name:'视频广告'},
    //                 {value:274, name:'联盟广告'},
    //                 {value:310, name:'邮件营销'},
    //                 {value:335, name:'直接访问'},
    //                 {value:400, name:'搜索引擎'}
    //             ],
    //             roseType: 'angle',
    //             itemStyle: {
    //                 normal: {
    //                     shadowBlur: 200,
    //                     shadowColor: 'rgba(0, 0, 0, 0.5)'
    //                 }
    //             }
    //         }
    //     ]
    // })
}

// 雷达图
{
    function drawEcharts3(population,distance,POI,price) {
        echarts.init($('#echarts3')).setOption({
            tooltip: { // 提示框
              confine: true,
              backgroundColor: "#fff",
              textStyle: {
                color: "#333",
                fontSize: 16,
                lineHeight: 16
              },
              padding: 15,
              borderWidth: 1,
              borderColor: "e1e3ec",
              axisPointer: {
                drossStyle: {
                  color: "#999"
                }
              },
              extraCssText: "box-shadow: 0 0 0.05rem 0 rgba(0, 0, 0, 0.1)" // 自定义属性
            },
            radar: { // 雷达图专用属性
                shape: "circle", // "polygon"多边形，"circle"圆型
                name: {
                    textStyle: {
                        color: 'rgb(173,205,112)',
                        fontSize: 20,
                        padding: [3, 5]
                    }
                },
                indicator: [ // 指示器
                    {name: '人口数据', max: 10},
                    {name: '距离', max: 10},
                    {name: 'POI(周边环境)', max: 10},
                    {name: '房价', max: 10}
                ]
            },
            areaStyle: { // 区域填充样式。（不写，默认不绘制）
              color: "rgb(175,206,113)",
              opacity: 0.7, // 0时，为不绘制图形
            },
            series: [{
                name: '能力',
                type: 'radar',
                areaStyle: {normal: {}},
                data: [
                    {
                        value: [population, distance, POI, price],
                        itemStyle: {
                          normal: {
                            color: "rgb(100,100,100)" // 雷达构成的区域边框
                          }
                        }
                    }
                ]
            }]
        })
    }

    setTimeout(() => {
        // 伪造数据
        drawEcharts3(85,40,65,90,10)

        fetch(BASE_URL+`/draw/leidatu/${vue.storeId}`,{
            mode: 'cors'
        })
        .then(res => res.json())
        .then(res => {
            let distance = res.data.distance
            let housePrice = res.data.housePrice
            let population = res.data.population
            let surrounds = res.data.surrounds
            drawEcharts3(population, distance, surrounds, housePrice)
        })
    }, 400);
}

// 热力图/百度地图

var newMap = function (lng,lat) {
    // 百度地图API功能
    map = new BMap.Map("map");
    //设置打开后的中心位置，这里设置的是北京
    var point = new BMap.Point(lng,lat);
    // var point = new BMap.Point(125.339517,43.958773);
    //设置打开后的缩放大小，这里设置的大致为中国版图的大小
    map.enableScrollWheelZoom(); //启用滚轮放大缩小
    map.centerAndZoom(point, 19);
    // 编写自定义函数,创建标注
    function addMarker(point){
        let marker = new BMap.Marker(point);
        map.addOverlay(marker);
    }
    //lat是纬度，lng是经度，count是对应的数值大小
    //下列的经纬度基本覆盖了中国各个省份及重要城市
    heatmapOverlay = new BMapLib.HeatmapOverlay({"radius": 150, 'opacity': .8});
    map.addOverlay(heatmapOverlay);
    //调整max的值效果会不一样，但count值一定要在max值之内
    // heatmapOverlay.setDataSet({data: hotMapArr,max:100});
    // heatmapOverlay.toggle()
    
    //设置地图样式，样式包括地图底图颜色和地图要素是否展示两部分
    map.setMapStyle({style:'light'})

    var styleJson = [ 
        {
            "featureType": "water",
            "elementType": "geometry.fill",
            "stylers": {
                "color": "#72E1FD", // #72E1FD 或 #113549
                "hue": "#2a8df8",
                "weight": "1",
                "lightness": 1,
                "saturation": 100,
                "visibility": "on"
            }
        },
        {
            "featureType": "land",
            "elementType": "geometry",
            "stylers": {
                "visibility": "on",
                "color": "#333333"
            }
        }
    ]
    map.setMapStyle({
        styleJson
    })

}
