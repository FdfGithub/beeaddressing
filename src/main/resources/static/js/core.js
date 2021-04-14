const $ = a=>document.querySelector(a)

const vue = new Vue({
    el: '#app',
    data: {
        cityJson,
        index: 0, // 轮播图的index
        listPage: 1, // 0为没有
        address: {
            // 已下的数据在未选择状态皆为null
            province: null,
            city: null,
            county: null, // 用户选择'无'则为 '无(不选)'
            cityId: null,
            countyId: null, // 用户选择'无'则为 -1
        },
        toggler: { // 下拉列表的数据
            // show即为下拉列表的下拉状态
            showPosSelector: false,
            // which即为选择状态, 但还没确定, 等用户选择了county才会确定
            whichProvince: null,
            whichCity: null,
            whichCityId: null,
        },
        countyList: null,
        // countyList = [
        //     {
        //         cityId: "fz"
        //         countyId: "lj"
        //         countyName: "连江"
        //         population: 30000
        //         timestamp: "2020-10-17T00:09:49"
        //     }
        // ]
        storeListInfo: [],
        hotMapArr: [],
        requestState: { // 'start'为请求开始, 'end'为请求结束
            storeList: null,
        },
        ellipsis: '。', // "数据请求中"后面的省略号
        // 辅助页面样式的数据
        style: {
            windowWidth: null, // 纯数字类型, 不带'px'
            showNav: false, // 导航栏的下拉状态
            map_max_min: 'max', // 地图的将要切换的状态(最小化和最大化), 'min' 和 'max
        },
    },
    methods: {
        left() {
            if(this.index === 0) {
                this.index = 2
            }
            else
                this.index--
        },
        right() {
            if(this.index === 2) {
                this.index = 0
            }
            else
                this.index++
        },
        clickDot(i) {
            this.index = i
        },
        selectProvince(province) {
            this.toggler.whichProvince = province
        },
        selectCity(obj) {
            this.toggler.whichCity = obj.city
            this.toggler.whichCityId = obj.cityId
        },
        selectCounty(obj) {
            this.address.county = obj.county
            this.address.countyId = obj.countyId
            // 将province和city从未确定状态设置到确定状态
            this.address.city = this.toggler.whichCity
            this.address.cityId = this.toggler.whichCityId
            // 关闭地址选择栏
            this.toggler.showPosSelector = false
        },
        // 用户选择'无'
        selectNoneCounty() {
            this.address.county = '无(不选)'
            this.address.countyId = -1
            // 将province和city从未确定状态设置到确定状态
            this.address.city = this.toggler.whichCity
            this.address.cityId = this.toggler.whichCityId
            // 关闭地址选择栏
            this.toggler.showPosSelector = false
        },
        changeStoreList({
            page,
            size=5,
            cityId,
            countyId
        }) {
            // 在地图上标出店铺的精准地标的函数
            function addMarker(lng_lat, text) { // lng_lat => '166.00,166.00'
                if (!map) {
                    alert('BMap丢失')
                    return
                }
                let lng = lng_lat.split(',')[0] * 1 // 经度
                let lat = lng_lat.split(',')[1] * 1 // 经度
                let point = new BMap.Point(lng, lat)
                let myIcon = new BMap.Icon("./pictures/red-location.png", new BMap.Size(32, 32))
                // 指定位置及标注的图标
                let marker = new BMap.Marker(point, { icon: myIcon })  // 创建标注
                if(text){
                    let label = new BMap.Label(text, { offset: new BMap.Size(32, -16) })
                    marker.setLabel(label)
                }
                // 添加到地图上
                map.addOverlay(marker)
            }

            // 修改this.storeListInfo
            if(countyId*1 === -1) { // 用户只给cityId,county选'无'时
                apiUtils.getStoreListInfoByCityId(cityId,page)
                .then(list => {
                    this.storeListInfo = this.storeListInfo.concat(list) // 更新列表数据

                    this.storeListInfo.forEach(storeInfo => { // 在地图上标出所有店铺的精准地标
                        addMarker(storeInfo.store.location,storeInfo.store.storeTitle)
                    })
                })
            }
            else{ // 用户给了cityId和countyId时
                apiUtils.getStoreListInfoByFullId(cityId,countyId,page)
                .then(list => {
                    this.storeListInfo = this.storeListInfo.concat(list) // 更新列表数据

                    this.storeListInfo.forEach(storeInfo => { // 在地图上标出所有店铺的精准地标
                        addMarker(storeInfo.store.location,storeInfo.store.storeTitle)
                    })
                })
            }
        },
        nextPageStoreList() {
            this.listPage ++ 
            this.changeStoreList({
                page: this.listPage,
                cityId: this.address.cityId,
                countyId: this.address.countyId,
            })
        },
        selectStore(storeInfo) {
            // 修改地图中心点
            let lng = storeInfo.store.location.split(',')[0] * 1 // 经度
            let lat = storeInfo.store.location.split(',')[1] * 1 // 经度
            let address = storeInfo.store.storeAddress // 如 万星广场-福州市连江县
            console.log('已选择地点: ',{lng,lat,address})
            var point = new BMap.Point(lng,lat)  // 创建点坐标    
            map.centerAndZoom(point, 15) // 初始化地图，设置中心点坐标和地图级别 
        },
        scrollPosSelector() {
            this.toggler.showPosSelector = !this.toggler.showPosSelector
        },
        map2max() {
            this.style.map_max_min = this.style.map_max_min === 'min' ? 'max' : 'min'
            if(this.style.map_max_min === 'min') {
                this.$refs.mapBox.style += `display: fixed; width: 100vw; height: 100vh;`
            }
            else{
                this.$refs.mapBox.style = ``
            }
        },
    },
    watch: {
        // 轮播图的index
        index(newIndex) {
            let imgWidth = this.$refs.innerBanner.offsetWidth / 3
            this.$refs.innerBanner.style.transform = `translateX(-${newIndex*imgWidth}px)`
        },
        whichCityId(newCityId,oldCityId) {
            if(newCityId === oldCityId) {
                return
            }
            apiUtils.getCountyListInfo(newCityId)
            .then(countyList => {
                this.countyList = countyList
            })
        },
        // 当countyId改变时, 要先判断cityId是否也变了, 避免出现不同城市的countyId重复的bug, 所以将county信息清空
        fullId(newFullId,oldFullId) {
            if(newFullId === oldFullId || newFullId*1 === -1) {
                return
            }
            let [newCityId,newCountyId] = newFullId.split('|')
            // 初始化列表
            this.listPage = 1
            this.storeListInfo = []
            this.changeStoreList({
                page: 1,
                cityId: newCityId,
                countyId: newCountyId
            })

            // 渲染热力图
            apiUtils.getHotMapArr(newCityId,newCountyId)
            .then(hotMapArr => {
                console.log('热力图对应的数据',hotMapArr)
                let pos = hotMapArr[0]
                hotMapArr = hotMapArr.slice(1)
                let point = new BMap.Point(pos.lng, pos.lat)
                map.centerAndZoom(point, 16)
                heatmapOverlay.setDataSet({data: hotMapArr, max:10})
            })
        },
        // countyId(newCountyId,oldCountyId) {
        //     if (newCountyId === oldCountyId || newCountyId === null) {
        //         return
        //     }
        //     this.listPage = 1
        //     this.storeListInfo = []
        //     this.changeStoreList({
        //         page: 1,
        //         cityId: this.address.cityId,
        //         countyId: newCountyId
        //     })
        // },
        storeListInfo(newStoreList,oldStoreList) {
            if(newStoreList === oldStoreList || newStoreList.length === 0) {
                return
            }

            // 按分数排序
            newStoreList.sort( (a,b) => {
                return b.score - a.score
            })
            
            // 储存到sessionStorage, key为storeId, value为json2Str
            newStoreList.forEach(storeInfo => {
                localStorage.setItem(
                    storeInfo.store.storeId,
                    JSON.stringify(storeInfo)
                )
            })
        },
    },
    mounted() {
        setTimeout(() => {
            // 动态修改windowWidth
            this.style.windowWidth = window.innerWidth
            window.onresize = () => {
                this.style.windowWidth = window.innerWidth
            }

            // 轮播图
            setInterval(() => {
                this.right()
            }, 4000);

            // ellipsis字符的设置
            setInterval(() => {
                let char = this.ellipsis[0]
                if(this.ellipsis.length === 3) {
                    this.ellipsis = char
                }
                else{
                    this.ellipsis = this.ellipsis + char
                }
            }, 500);
            
        }, 20);
    },
    computed: {
        countyId() {
            return this.address.countyId
        },
        whichCityId() {
            return this.toggler.whichCityId
        },
        fullId() {
            if(this.address.cityId && this.address.countyId) {
                return this.address.cityId + '|' + this.address.countyId
            }
            else
                return -1
        },
    },
})

const footerVue = new Vue({
    el: '#footer',
    data: {
        style: {
            windowWidth: null,
        }
    },
    mounted() {
        setTimeout(() => {
            // 动态修改windowWidth
            this.style.windowWidth = window.innerWidth
            window.onresize = () => {
                this.style.windowWidth = window.innerWidth
            }
        },20)
    },
})

// 地址选择栏, js设置样式
{
    window.onresize = () => {
        vue.style.windowWidth = window.innerWidth
    }

}







// 曲线图
{
    // echarts.init($('#echarts')).setOption( {
    //         title: {
    //             // text: '第一个 ECharts 实例'
    //         },
    //         tooltip: {},
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
    //             // type: 'bar'：柱状/条形图
    //             // type: 'line'：折线/面积图
    //             // type: 'pie'：饼图
    //             // type: 'scatter'：散点（气泡）图
    //             // type: 'effectScatter'：带有涟漪特效动画的散点（气泡）
    //             // type: 'radar'：雷达图
    //             // type: 'tree'：树型图
    //             // type: 'treemap'：树型图
    //             // type: 'sunburst'：旭日图
    //             // type: 'boxplot'：箱形图
    //             // type: 'candlestick'：K线图
    //             // type: 'heatmap'：热力图
    //             // type: 'map'：地图
    //             // type: 'parallel'：平行坐标系的系列
    //             // type: 'lines'：线图
    //             // type: 'graph'：关系图
    //             // type: 'sankey'：桑基图
    //             // type: 'funnel'：漏斗图
    //             // type: 'gauge'：仪表盘
    //             // type: 'pictorialBar'：象形柱图
    //             // type: 'themeRiver'：主题河流
    //             // type: 'custom'：自定义系列
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

// 热力图/百度地图
{
    // 百度地图API功能
    var map = new BMap.Map("map");
    //设置打开后的中心位置，这里设置的是北京
    var point = new BMap.Point(116.512885,39.847469);
    //设置打开后的缩放大小，这里设置的大致为中国版图的大小
    map.enableScrollWheelZoom(); //启用滚轮放大缩小
    map.centerAndZoom(point, 5);
    // 编写自定义函数,创建标注
    function addMarker(point){
        var marker = new BMap.Marker(point);
        map.addOverlay(marker);
    }
    //lat是纬度，lng是经度，count是对应的数值大小
    //下列的经纬度基本覆盖了中国各个省份及重要城市
    heatmapOverlay = new BMapLib.HeatmapOverlay({"radius": 50, 'opacity': .8});
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

    // 放大 map.zoomIn(); 缩小 map.zoomOut(); 指定倍数 map.setZoom(14)

    // 修改中心点
    // var point = new BMap.Point(121.479048,31.240008);  // 创建点坐标    
    // map.centerAndZoom(point, 11);// 初始化地图，设置中心点坐标和地图级别 
    
}