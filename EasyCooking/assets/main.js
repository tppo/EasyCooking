var drawMini = function (t_id) {
	var ingr_lst=(dbProvider.getRecipeIngredients(t_id)).split(' ');
	var len=parseInt(ingr_lst[0]);
	var ingr_txt=[];
	
	for(var i=1;i<=len;i++)
		ingr_txt[i]=dbProvider.getIngredientName(parseInt(ingr_lst[i]));
	
	var nm=dbProvider.getRecipeName(t_id);
	
	var t=document.createElement('div');
	
	//t.attr("width", "90%");
	t.setAttribute("width", "90%")
	t.onclick = function(){
		fullPage(t_id);	
	}
	var str=nm.toString()+"\n";
	str+="<";
	for(var i=1;i<=len;i++){
		str+=ingr_txt[i];
		if(i!=len) str+=", "
	}
	str+=">";
	t.innerHTML=str;
	t.className="miniRec";
	document.getElementById('mainSection').appendChild(t);
	
}
var cookButtonHandler=function(){
	var str=document.getElementById('searchInput').value;
	var mnSctn=document.getElementById('mainSection');
	$(mnSctn).empty();
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
	$(mnSctn).empty();
	//var last;
  //  while (last = mnSctn.lastChild) 
    //	mnSctn.removeChild(last);
	var lst=dbProvider.getRecipes().split(' ');
	var t=parseInt(lst[0]);
	for(var i=1;i<=t;i++)
		drawMini(parseInt(lst[i]));
};
var favouriteHandler = function () {
	var mnSctn=document.getElementById('mainSection');
	$(mnSctn).empty();
	var last;
   // while (last = mnSctn.lastChild) 
    //	mnSctn.removeChild(last);
	var lst=dbProvider.getFavouriteList().split('');
	var t=parseInt(lst[0]);
	for(var i=1;i<=t;i++)
		drawMini(parseInt(lst[i]));
	
};

var toBuyHandler = function () {
	var mnSctn=document.getElementById('mainSection');
	$(mnSctn).empty();
	//var last;
   // while (last = mnSctn.lastChild) 
    	//mnSctn.removeChild(last);
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
	$(mnSctn).empty();
	
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
	console.log("fullpaging "+t_id);
	var mnSctn=document.getElementById('mainSection');
	$(mnSctn).empty();
	//var last;
   // while (last = mnSctn.lastChild) 
    //	mnSctn.removeChild(last);

	var ingr_lst=(dbProvider.getRecipeIngredients(t_id)).split(' ');
	var nm=dbProvider.getRecipeName(t_id);
	var dcr=dbProvider.getDescription(t_id);
	
	
	var tmp=document.createElement('div');
	tmp.setAttribute("scroll", "yes");
	
	var tl=document.createElement('h1');
	tl.setAttribute("font-size","20px");
	tl.innerHTML=nm;
	tmp.appendChild(tl);
	
	var tgs=document.createElement('p');
	tgs.innerHTML="tags: ";
	tgs.setAttribute("font-size","12px");
	tgs.setAttribute("align","right");
	
	var tgs_lst=dbProvider.getRecipeTags(t_id).split(' ');
	for(var i=1;i<=tgs_lst[0];i++)
		tgs.innerHTML+=dbProvider.getTagName(tgs_lst[i])+" ";
	tmp.appendChild(tgs);
	
	
	var p=document.createElement('p');
	p.setAttribute("font-size","14");
	for(var i=1;i<=ingr_lst[0];i++)
		p.innerHTML+=dbProvider.getIngredientName(ingr_lst[i])+" ";
	tmp.appendChild(p);
	
	
	
	var d=document.createElement('p');
	d.setAttribute("font-size","14");
	d.innerHTML=dcr;
	tmp.appendChild(d);
	
	
	document.getElementById('mainSection').appendChild(tmp);
	
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


