/***********************
* Script functions (interaction with the server)
************************/

var readReqeust, notQueryRequest;

var richiestaSugg, similarRequest;

//

function access()
{
    document.formLogin.action = "/access";  
    document.formLogin.submit();	
}

//modificato qui!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
function deleteArtwork(id)
{
	notQueryRequest = new XMLHttpRequest();
	var cod =id ;
	var url="delArtwork?id="+encodeURIComponent(cod);
	notQueryRequest.open("GET", url, true);
	notQueryRequest.onreadystatechange = notQueryUpdate;
	notQueryRequest.send(null);
}

function addArtwork(tit,aut,abs,pic)
{
	notQueryRequest = new XMLHttpRequest();
	var title =tit ;
	var author =aut ;
	var pictureAbstract =abs;
	console.log("!!!!! "+abs+" "+pictureAbstract);
	var pictureurl=pic;
	var url="insertArtwork?title="+encodeURIComponent(title)+"&author="+encodeURIComponent(author)+"&pictureAbstract="+encodeURIComponent(pictureAbstract)+"&pictureUrl="+encodeURIComponent(pictureurl);
	notQueryRequest.open("GET", url, true);
	notQueryRequest.onreadystatechange = notQueryUpdate;
	notQueryRequest.send(null);
	
}

function updateArtwork(tit,aut,abs,pic)
{
	notQueryRequest = new XMLHttpRequest();
	var title =tit;
	var author =aut;
	var picAbstract =abs;
	var pictureurl=pic;
	var url="updArtwork?title="+encodeURIComponent(title)+"&author="+encodeURIComponent(author)+"&pictureAbstract="+encodeURIComponent(picAbstract)+"&pictureUrl="+encodeURIComponent(pictureurl);
	notQueryRequest.open("GET", url, true);
	notQueryRequest.onreadystatechange = notQueryUpdate;
	notQueryRequest.send(null);
}

function readUpdate()
{
	if (readReqeust.readyState == 4 && readReqeust.status == 200)
	{
		var response = JSON.parse(readReqeust.responseText);
		var artworks = response;
		var i = 0;
		cleanTable();
		for (var counter in artworks)
		{
			showArtwork(artworks, i);
			i++;
		}
	}
}

function notQueryUpdate()
{
	if (notQueryRequest.readyState == 4 && notQueryRequest.status == 200)
	{
		var response = notQueryRequest.responseText;
	}
}

function showArtworks()
{
	readReqeust = new XMLHttpRequest();
	var url="/allArtworks";
	readReqeust.open("GET", url, true);
	readReqeust.onreadystatechange = readUpdate;
	readReqeust.send(null);
}

function searchArtworks(partial)
{
	readReqeust = new XMLHttpRequest();
	var title =partial;
	var author = partial;
	var url="/searchArtwork?title="+encodeURIComponent(title)+"&author="+encodeURIComponent(author);
	readReqeust.open("GET", url, true);
	readReqeust.onreadystatechange = readUpdate;
	readReqeust.send(null);
}

function similarArtworks(auth, tit)
{
	similarRequest = new XMLHttpRequest();
	var author = auth;
	var title = tit;
	var url="/similarArtworks?title="+encodeURIComponent(title)+"&author="+encodeURIComponent(author);
	similarRequest.open("GET", url, true);
	similarRequest.onreadystatechange = similarArtworksUpdate;
	similarRequest.send(null);
}

function similarArtworksUpdate()
{
	if (similarRequest.readyState == 4 && similarRequest.status == 200)
	{
		var response = JSON.parse(similarRequest.responseText);
		var artworks = response;
		var i = 0;
		for (var counter in artworks)
		{
			showSimilar(artworks, i);
			i++;
		}
		if (i == 0)
			showSimilar("", -1);
	}
}

/////////////////////////////suggerimenti google
function ricerca()
{
    richiestaSugg = new XMLHttpRequest();
    var valore = document.getElementById("txtSearch").value;
    var url = "completion?parolaCercata=" + encodeURIComponent(valore);
	richiestaSugg.open("get", url, true);
	richiestaSugg.onreadystatechange = aggiornaPag;
	richiestaSugg.send(null);
}

function aggiornaPag()
{
    if(richiestaSugg.readyState == 4 && richiestaSugg.status == 200)
    {
        var risposta = JSON.parse(richiestaSugg.responseText); // readReqeust arrivata dal server
        console.log(risposta);
        var tab = document.getElementById("lstSuggerimenti");
            tab.innerHTML = "";
            tab.style.display = "none";
        if(risposta != "")
        {
            tab.style.height = (21 * risposta.length) + "pt";
            for(var i=0; i<risposta.length; i++)
            {
                var riga = document.createElement("option");
                riga.textContent = risposta[i].voce;
                
                tab.appendChild(riga);
            }
            tab.style.display = "block";
        }
        
    }
}



