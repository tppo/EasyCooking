var drawMini = function (t_id) {
	var ingr_lst=(dbProvider.getRecipeIngredients(t_id)).split(' ');
	var len=parseInt(ingr_lst[0]);
	var ingr_txt=[];
	
	for(var i=1;i<=len;i++)
		ingr_txt[i]=dbProvider.getIngredientName(parseInt(ingr_lst[i]));
	
	var nm=dbProvider.getRecipeName(t_id);
	
	var t=document.createElement('div');
	var str=nm.toString()+"\n";
	for(var i=1;i<=len;i++)
		str+=ingr_txt[i]+' ';
	t.innerHTML=str;
	
}
var cookButtonHandler=function(){
	var str=document.getElementById('searchInput').value;
	var lst=dbProvider.getRecipeId(str).split(' ');
	var t=parseInt(lst[0]);
	for(i=1;i<=t;i++)
		drawMini(parseInt(lst[i]));
	
};

var allRecipesHandler = function() {
	var mnSctn=document.getElementById('mainSection');
	var lst=dbProvider.getRecipes().split(' ');
	var t=parseInt(lst[0]);
	for(i=1;i<=t;i++)
		drawMini(parseInt(lst[i]));
};
var favouriteHandler = function () {
	
	var mnSctn=document.getElementById('mainSection');
	
};

var toBuyHandler = function () {
	var mnSctn=document.getElementById('mainSection');
	
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

var fullPage = function () {
	
	
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


