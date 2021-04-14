#E选址接口文档

## 统一返回对象，JSON格式
    public class Response {
        状态 true：代表成功, false:代表失败
        private boolean status;
        
        前端如果需要对错误单独处理，就需要指定状态码
        private int code;
        
        响应的数据对象
        private Object data;
        
        // 消息提示
        private String msg;
    }


## 用户模块

### 1,获取验证码

	GET /user/sendValidateCode

	参数
		tel: 手机号		Type:string
	成功返回: 
	    {
	         status: true
	         code: 
	         data:
             msg: "验证码发送成功"
	    }
	错误返回:
        {
             status: false
             code:
             data:
             msg: "验证码发送失败"
        }

### 2,注册

	POST /user/register

	参数
		tel: 手机号 			    Type:string
		password: 密码 (6到20位)		Type:string
		code: 验证码					Type:string
	成功返回:
        {
             status: true
             code: 
             data: 
             msg: "注册成功"
        }
	错误返回:
        {
             status: false
             code: 
             data: 
             msg: "注册失败"
        }



### 3,登录

	POST /user/login

	参数
		tel: 手机号			Type:string
		password: 密码			Type:string
	成功返回value:
        {
             status: true
             code:
             data: {
                  userId：  
                  userName,
                  userHeadUrl
             }
             msg: "登录成功"
        }
	错误返回:
        {
             status: false
             code:
             data:
             msg: "用户名或密码错误"
        }
        

## 商铺模块

## 1,获取地区列表

	GET /{cityId}/county
	cityId：城市id
    例如：/fz/county
    
	参数：
	{
	}

	成功返回:
	{
          status: true
          code:
          data: [
               countyId: 地区id
               countyName: 地区名称
          ]
          msg: "获取成功"
	}

	失败返回
	{
          status: false
          code:
          data: 
          msg: "获取失败"
	}

## 2,获取商铺列表

	GET /store/{countyId}/{page}/{size} 
    countyId：地区id
    page: 当前页数
    size: 每页的大小
	例如：/sotre/lj/0/20  

	参数：
	{
	}
	成功返回:
	{
	    status: true
        data: [
               商铺信息对象
               store：{
                     storeId：商铺id
                     cityId：城市id
                     countyId：区id
                     location：经纬度
                     storeTitle：房源标题
                     storeImg：商铺图片
                     storeArea：商铺面积
                     areaTyp： 面积单位
                     storeAddress：商铺地址        
                     storeRent：商铺租金
                     rentType： 租金单位
                     publisherName：联系人姓名
                     publisherTel：发布人联系方式
                     isFavorite：是否收藏  
                     rentState：出租状态
               }
               score：分值
               各个评分点的分值
               scores：[ 
                    type: 评分类型；例如：客流量
                    score：分值
                    reason：原因
                    referenceData：参考的数据       
               ]
        ]
	}
	失败返回
	{
		status: false
	}

