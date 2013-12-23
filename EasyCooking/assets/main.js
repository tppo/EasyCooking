var drawMini = function (t_id) {
	var ingr_lst=(dbProvider.getRecipeIngredients(t_id)).split(' ');
	var len=parseInt(ingr_lst[0]);
	var ingr_txt=[];
	
	for(var i=1;i<=len;i++)
		ingr_txt[i]=dbProvider.getIngredientName(parseInt(ingr_lst[i]));
	
	var nm=dbProvider.getRecName(t_id);
	
	var t=document.createElement('div');
	
	t.attr("width", "90%");
	t.click(function(){
		fullPage(t_id);	
	});
	var str=nm.toString()+"\n";
	for(var i=1;i<=len;i++)
		str+=ingr_txt[i]+' ';
	t.innerHTML=str;
	document.getElemetById('mainSection').appendChild(t);
	
}
var cookButtonHandler=function(){
	var str=document.getElementById('searchInput').value;
	var mnSctn=document.getElementById('mainSection');
	mnSctn.empty();
	var lst=dbProvider.getRecipeId(str).split(' ');
	var t=parseInt(lst[0]);
	if(t==0) {
		var tmp=document.createElement('p');
		tmp.innerHTML="Sorry, such recipe was not found";
		mnSctn.appendChild(tmp);
	}
	else {
		for(var i=1;i<=t;i++)
			drawMini(parseInt(lst[i]));
	}
	
};

var allRecipesHandler = function() {
	var mnSctn=document.getElementById('mainSection');
	mnSctn.empty();
	var lst=dbProvider.getRecipes().split(' ');
	var t=parseInt(lst[0]);
	for(var i=1;i<=t;i++)
		drawMini(parseInt(lst[i]));
};
var favouriteHandler = function () {
	var mnSctn=document.getElementById('mainSection');
	mnSctn.empty();
	var lst=dbProvider.getFavouriteList().split('');
	var t=parseInt(lst[0]);
	for(var i=1;i<=t;i++)
		drawMini(parseInt(lst[i]));
	
};

var toBuyHandler = function () {
	var mnSctn=document.getElementById('mainSection');
	mnSctn.empty();
	var lst=dbProvider.getBuyList().split(' ');
	var t=parseInt(lst[0]);
	var str;
	for(var i=1;i<=t;i++){
		str=dbProvider.getIngredientName(parseInt(lst[i]));
		var tmp=document.createElement('a');
		tmp.innerHTML=str;
		mnSctn.append(tmp);
	}
			
};
var homeHandler = function ()  {
	var mnSctn=document.getElementById('mainSection');
	
};

var addToFaveHandler = function (name) {
	
};

var removeFromFaveHandler = function (name) {
	
};

var allToBuyHandler = function (name) {
	
};

var thisToBuyHandler = function (ingr) {
	
};

var removeFromToBuyHandler = function (ingr) {
	
};

var searchHandler = function () {
	
};

var fullPage = function (t_id) {
	var mnSctn=document.getElementById('mainSection');
	mnSctn.empty();
	var ingr_lst=(dbProvider.getRecipeIngredients(t_id)).split(' ');
	var nm=dbProvider.getRecName(t_id);
	var dcr=dpProvider.getDescription(t_id);
	
	
	var tmp=document.createElement('div');
	tmp.attr("scroll", "yes");
	
	var tl=document.createElement('h1');
	t1.attr("font-size","20px");
	t1.innerHTML=mn;
	tmp.append(t1);
	
	var tgs=document.createElement('p');
	tgs.innerHTML="tags: ";
	tgs.attr("font-size","12px");
	tgs.attr("align","right");
	
	var tgs_lst=dpProvider.getRecipeTags(t_id);
	for(var i=1;i<=tgs_lst[0];i++)
		tgs.innerHTML+=dpProvider.getTagName(tgs_lst[i]+ " ");
	tmp.append(tgs);
	
	
	var p=document.createElement('p');
	p.attr("font-size","14");
	for(var i=1;i<=ingr_lst[0];i++)
		p.innerHTML+=dpProvider.getIngredientName(ingr_lst[i])+" ";
	tmp.append(p);
	
	
	
	var d=document.createElement('p');
	d.attr("font-size","14");
	d.innerHTML=dcrp;
	tmp.append(d);
	
	
	
	
};



var init = function () {
    // TODO:: Do your initialization job
   // console.log("init() called");
	//console.log("_________________________________________________________________________");
    // add eventListener for tizenhwkey
  document.addEventListener('tizenhwkey', function(e) {
      if(e.keyName == "back")
        tizen.application.getCurrentApplication().exit();
  });
     
    
    document.getElementById('cookButton').onclick=cookButtonHandler;
    document.getElementById('allRecipes').onclick=allRecipesHandler;
    document.getElementById('favourites').onclick=favouriteHandler;
    document.getElementById('toBuyList').onclick = toBuyHandler;
    document.getElementById('toHome').onclick 	= 	homeHandler;
        
};

$(document).ready(init);


