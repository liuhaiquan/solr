
$(document).ready(function(){



    $('.reptile').on('click',function(){
        $.ajax({
            url:"http://localhost:9999/reptile",    //请求的url地址
            dataType:"text",   //返回格式为json
            async:true,//请求是否异步，默认为异步，这也是ajax重要特性
            type:"POST",   //请求方式
            success:function(data){
                alert(data);
            },
            error:function(){
                alert("请求失败");
            }
        });
    })



    $('.createIndex').on('click',function(){
        $.ajax({
            url:"http://localhost:9999/createIndex",    //请求的url地址
            dataType:"text",   //返回格式为json
            async:true,//请求是否异步，默认为异步，这也是ajax重要特性
            type:"POST",   //请求方式
            success:function(data){
                alert(data);
            },
            error:function(){
                alert("请求失败");
            }
        });
    })


    var fnTemplate =  Handlebars.compile($('.content-template').html());
    Handlebars.registerHelper('parseText', function(text, options) {
        return new Handlebars.SafeString(text);
    });

    $('.search').on('click',function(){
        var queryKey =$("input[name=queryKey]").val();
        var data = JSON.stringify({"queryKey":queryKey});
        $.ajax({
            url:"http://localhost:9999/search",    //请求的url地址
            type:"POST",   //请求方式
            contentType: 'application/json; charset=UTF-8',
            async:false,
            dataType:'json', //请求是否异步，默认为异步，这也是ajax重要特性
            data:data,
            success:function(data){
               $('.content-area').html(fnTemplate(data));
            },
            error:function(){
                alert("请求失败");
            }
        });
    })

});