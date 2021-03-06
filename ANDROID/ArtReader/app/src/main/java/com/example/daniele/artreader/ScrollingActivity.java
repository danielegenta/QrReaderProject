package com.example.daniele.artreader;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class ScrollingActivity extends AppCompatActivity {

   // ArrayList<Artwork> listHistory = new ArrayList<Artwork>();
    ArrayList<Artwork> listFavourites = new ArrayList<Artwork>();
    MediaPlayer mp;
    boolean isAudioPlaying = false;
    boolean privateSession = false;
    int idArtwork;



    //da cambiare ogni volta (come invia richiesta http)
    String myIp;


    String auxTitle = "", auxAuthor= "", auxArtMovement = ""; int auxId = 0;
    Lists myLists = null;

    String retVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myIp = ((MyApplication)getApplicationContext()).myUrl;

        Bundle b = getIntent().getExtras();
        String strHistory  =  b.getString("jsonHistory");
        String strFavourites  =  b.getString("jsonFavourites");
        String strJson  =  b.getString("jsonArtwork");

        privateSession = b.getBoolean("privateSession");

       /* try
        {
            //JSONObject jObj = new JSONObject(strHistory);
            JSONArray jArr = new JSONArray(strHistory);
            for (int i=0; i < jArr.length(); i++)
            {
                JSONObject obj = jArr.getJSONObject(i);
                int id = obj.getInt("id");
                String title = obj.getString("title");
                String author = obj.getString("author");
                String img = obj.getString("img");
                Artwork a = new Artwork(id, title, author, img);
                listHistory.add(a);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
*/
        try
        {
            JSONArray jArr = new JSONArray(strFavourites);
            for (int i=0; i < jArr.length(); i++)
            {
                JSONObject obj = jArr.getJSONObject(i);
                int id = obj.getInt("id");
                String title = obj.getString("title");
                String author = obj.getString("author");
                String img = obj.getString("img");
                Artwork a = new Artwork(id, title, author, img);
                listFavourites.add(a);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        myLists = new Lists();
        initialSettings();
        loadData(strJson);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (mp != null)
            mp.stop();
    }


    private void initialSettings()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setTag("grey");
        fab.setImageResource(R.drawable.favouritegrey);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFavourite(view);
            }
        });

        //lettura dei file di testo
        readFromFile("history.txt");
        String aux1 = readFromFile("history.txt");
        if (aux1 != "")
        {
            String righe1[] = aux1.split(";");
            String[] colonne1;
            for (int i = 0; i < righe1.length; i++)
            {
                colonne1 = righe1[i].split("-");
                myLists.addHistoryRecord((new Artwork(Integer.parseInt(colonne1[0]), colonne1[1], colonne1[2], colonne1[3])));
            }
        }
    }

    private String readFromFile(String filename)
    {
        String ret = "";
        try
        {
            InputStream inputStream = openFileInput(filename);

            if ( inputStream != null )
            {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null )
                {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
            //CREO IL FILE
            else
            {
                writeToFile("", filename);
            }
        }
        catch (FileNotFoundException e)
        {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e)
        {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }

    /**************DISPLAY ARTWORK INFO : AFTER SCAN**********************/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void loadData(String strJson)
    {
        CollapsingToolbarLayout mainTitle =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        //TextView id = (TextView)findViewById(R.id.lblIdArtwork);
        TextView title = (TextView)findViewById(R.id.lblTitleArtwork);
        TextView author = (TextView)findViewById(R.id.lblAuthorArtwork);
        TextView tecnique = (TextView)findViewById(R.id.lblTecnique);
        TextView dimensions = (TextView)findViewById(R.id.lblDimensions);
        TextView info = (TextView)findViewById(R.id.lblInfo);
        TextView location = (TextView)findViewById(R.id.lblLocation);
        TextView address = (TextView)findViewById(R.id.lblAddress);
        TextView year = (TextView)findViewById(R.id.lblYear);
        TextView artMovement = (TextView)findViewById(R.id.lblArtMovement);
        TextView description = (TextView)findViewById(R.id.lblDescription);
        ImageView headerImg = (ImageView) findViewById(R.id.bgheader);
        ImageView imgArtwork = (ImageView)findViewById(R.id.imgArtwork);

        //per far funzionare i link
        location.setMovementMethod(LinkMovementMethod.getInstance());
        author.setMovementMethod(LinkMovementMethod.getInstance());
        info.setMovementMethod(LinkMovementMethod.getInstance());


        try
        {
            JSONObject jsonRootObject = new JSONObject(strJson);

            //popolo campi
            idArtwork = Integer.parseInt(jsonRootObject.optString("id"));

            mainTitle.setTitle(jsonRootObject.optString("title"));
            title.setText(jsonRootObject.optString("title"));
            author.setText(Html.fromHtml("<a href=\"" + jsonRootObject.optString("wikipediaPageAuthor") + "\">" + (jsonRootObject.optString("name")) + "</a> "));
            description.setText(jsonRootObject.optString("abstract"));
            tecnique.setText(jsonRootObject.optString("tecnique"));
            year.setText(jsonRootObject.optString("year"));
            artMovement.setText(jsonRootObject.optString("artMovement"));
            dimensions.setText(jsonRootObject.optString("dimensionWidth") + "x" + jsonRootObject.optString("dimensionHeight") );
            info.setText(Html.fromHtml("<a href=\""+jsonRootObject.optString("wikipediaPageArtwork")+"\">Ulteriori Informazioni</a> "));
            location.setText(Html.fromHtml("<a href=\"" + jsonRootObject.optString("wikipediaPageLocation") + "\">" + (jsonRootObject.optString("description")) + "</a> "));
            address.setText(jsonRootObject.optString("address"));

            //immagine
            String imgName = jsonRootObject.optString("pictureUrl");
            Picasso.with(getApplicationContext()).load(myIp +"img/immagini/"+imgName).into(imgArtwork);
            imgArtwork.setTag(imgName);



            //header img
            imgName = jsonRootObject.optString("pictureUrl3");
            Picasso.with(getApplicationContext())
                    .load(myIp + "img/parallax/" + imgName)
                    .into(headerImg);


            //utili per ricerche correlate
            auxTitle = title.getText().toString();
            auxAuthor = jsonRootObject.optString("author"); //mi serve id autore
            auxArtMovement = artMovement.getText().toString();
            auxId=idArtwork;

            //verifico se è gia nei preferiti, se sì, cambio colore icona
            if (listFavourites != null)
            {
                for (Artwork a : listFavourites)
                {
                    if (Integer.valueOf(a.getID()) == Integer.valueOf(idArtwork))
                    {
                        setFavouriteSupport();
                        break;
                    }
                }
            }
            addHistoryRecord();
            scriviModifiche("history");

            loadRelatedSearchMainRequest();

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void loadRelatedSearch(JSONObject obj, int index)
    {
        if (index == 0)
        {
            //definisco oggetti che mi servono
            TextView textRelatedSearch1 = (TextView)findViewById(R.id.txtRelatedSearch1);
            ImageView imageRelatedSearch1 = (ImageView) findViewById(R.id.imgRelatedSearch1);

            //setto visibilità
            textRelatedSearch1.setVisibility(View.VISIBLE);
            imageRelatedSearch1.setVisibility(View.VISIBLE);

            //
            String id = obj.optString("id");
            imageRelatedSearch1.setTag(id);

            //setto attributi
            textRelatedSearch1.setText(obj.optString("title"));
            String imgName = obj.optString("pictureUrl");
            Picasso.with(getApplicationContext()).load(myIp +"img/immagini/"+imgName).into(imageRelatedSearch1);

            //regolare dimensioni immagine da codice
            imageRelatedSearch1.requestLayout();
            imageRelatedSearch1.getLayoutParams().height = 300;
            imageRelatedSearch1.getLayoutParams().width = 300;
        }
        else if (index == 1)
        {
            //definisco oggetti che mi servono
            TextView textRelatedSearch2 = (TextView)findViewById(R.id.txtRelatedSearch2);
            ImageView imageRelatedSearch2 = (ImageView) findViewById(R.id.imgRelatedSearch2);

            //setto visibilità
            textRelatedSearch2.setVisibility(View.VISIBLE);
            imageRelatedSearch2.setVisibility(View.VISIBLE);

            //
            String id = obj.optString("id");
            imageRelatedSearch2.setTag(id);

            //setto attributi
            textRelatedSearch2.setText(obj.optString("title"));
            String imgName = obj.optString("pictureUrl");
            Picasso.with(getApplicationContext()).load(myIp +"img/immagini/"+imgName).into(imageRelatedSearch2);

            //regolare dimensioni immagine da codice
            imageRelatedSearch2.requestLayout();
            imageRelatedSearch2.getLayoutParams().height = 300;
            imageRelatedSearch2.getLayoutParams().width = 300;
        }
        else if (index == 2)
        {
            //definisco oggetti che mi servono
            TextView textRelatedSearch3 = (TextView)findViewById(R.id.txtRelatedSearch1);
            ImageView imageRelatedSearch3 = (ImageView) findViewById(R.id.imgRelatedSearch1);

            //setto visibilità
            textRelatedSearch3.setVisibility(View.VISIBLE);
            imageRelatedSearch3.setVisibility(View.VISIBLE);

            //
            String id = obj.optString("id");
            imageRelatedSearch3.setTag(id);

            //setto attributi
            textRelatedSearch3.setText(obj.optString("title"));
            String imgName = obj.optString("pictureUrl");
            Picasso.with(getApplicationContext()).load(myIp +"img/immagini/"+imgName).into(imageRelatedSearch3);

            //regolare dimensioni immagine da codice
            imageRelatedSearch3.requestLayout();
            imageRelatedSearch3.getLayoutParams().height = 300;
            imageRelatedSearch3.getLayoutParams().width = 300;
        }
    }
    /****************************END DISPLAY ARTWORK INFO*****************/

    /******************
     * GESTIONE PREFERITI
     ******************/
    private void addHistoryRecord()
    {
        TextView title = (TextView) findViewById(R.id.lblTitleArtwork);
        TextView author = (TextView) findViewById(R.id.lblAuthorArtwork);
        ImageView img = (ImageView) findViewById(R.id.imgArtwork);
        String imgName = img.getTag().toString();
        Artwork artwork = new Artwork(idArtwork, title.getText().toString(), author.getText().toString(), imgName);
        int index = 0;

        //rimuovo eventuai occorrenze
        if (myLists.listArtworksHistory != null)
        {
            for (Artwork a : myLists.listArtworksHistory)
            {
                if (Integer.valueOf(a.getID()) == Integer.valueOf(idArtwork))
                {
                    myLists.listArtworksHistory.remove(a);
                    break;
                }
            }
        }
        //aggiungo in testa
        myLists.listArtworksHistory.add(0, artwork);

    }

    public void setFavourite(View v)
    {
        boolean red = setFavouriteSupport();
        TextView title = (TextView) findViewById(R.id.lblTitleArtwork);
        TextView author = (TextView) findViewById(R.id.lblAuthorArtwork);
        ImageView img = (ImageView) findViewById(R.id.imgArtwork);
        String imgName = img.getTag().toString();

        Artwork artwork = new Artwork(idArtwork, title.getText().toString(), author.getText().toString(), imgName);

        //aggiornare struttura
        //aggiunta a preferiti
        if (red)
        {
            //aggiunta elemento a lstPreferiti
            listFavourites.add(artwork);
        }
        //rimozione da preferiti
        else
        {
            int index = 0;
            //eliminazione da struttura logica
            for (Artwork a:listFavourites)
            {
                if (a.getID() == idArtwork)
                    break;
                index++;
            }
            listFavourites.remove(index);
        }
        scriviModifiche("favourites");
    }

    private boolean setFavouriteSupport()
    {
        //ImageView imgFavourite = (ImageView)findViewById(R.id.imgFavourite);
        FloatingActionButton imgFavourite = (FloatingActionButton)findViewById(R.id.fab);
        String tag = (String)imgFavourite.getTag();
        if (tag.compareTo("grey") == 0)
        {
            imgFavourite.setImageResource(R.drawable.favouritered);
            imgFavourite.setTag("red");

            return  true;
        }
        else
        {
            imgFavourite.setImageResource(R.drawable.favouritegrey);
            imgFavourite.setTag("grey");

            return false;
        }
    }
    /***********FINE GESTIONE PREFERITI*******/

    /*****************GESTIONE AGGIORNAMENTO CRONOLOGIA E PREFERITI**************/
    private void scriviModifiche(String fileToUpdate)
    {
        int index = 0;
        if (fileToUpdate.compareTo("history") == 0)
        {
            if (myLists.listArtworksHistory != null)
            {
                if (!privateSession)
                {
                    if (myLists.listArtworksHistory.size() > 0)
                    {
                        String data = "";
                        String filename = "history1.txt";
                        for (Artwork a : myLists.listArtworksHistory)
                        {
                            if (index == 0)
                                data = a.getID() + "-" + a.getTitle() + "-" + a.getAuthor() + "-" + a.getImg_path();
                            else
                                data += ";"+a.getID() + "-" + a.getTitle() + "-" + a.getAuthor() + "-" + a.getImg_path();
                            index++;
                        }
                        writeToFile(data, filename);
                        RenameAppFile(getApplicationContext(), "history1.txt", "history.txt");
                    }
                    else
                    {
                        String data = "";
                        String filename = "history1.txt";
                        writeToFile(data, filename);
                        RenameAppFile(getApplicationContext(), "history1.txt", "history.txt");
                    }
                }
            }
        }
        else
        {
            if (listFavourites != null)
            {
                if (listFavourites.size() > 0)
                {
                    String data = "";
                    for (Artwork a : listFavourites)
                    {
                        if (index == 0)
                            data = a.getID() + "-" + a.getTitle() + "-" + a.getAuthor() + "-" + a.getImg_path();
                        else
                            data += ";"+a.getID() + "-" + a.getTitle() + "-" + a.getAuthor() + "-" + a.getImg_path();
                        index++;
                    }
                    String filename = "favourites1.txt";
                    writeToFile(data, filename);
                    RenameAppFile(getApplicationContext(), "favourites1.txt", "favourites.txt");
                }
                else
                {
                    String data = "";
                    String filename = "favourites1.txt";
                    writeToFile(data, filename);
                    RenameAppFile(getApplicationContext(), "favourites1.txt", "favourites.txt");
                }
            }
        }
    }

    public static void RenameAppFile(Context context, String originalFileName, String newFileName)
    {
        File originalFile = context.getFileStreamPath(originalFileName);
        File newFile = new File(originalFile.getParent(), newFileName);
        if (newFile.exists())
        {
            context.deleteFile(newFileName);
        }
        originalFile.renameTo(newFile);
    }

    private void writeToFile(String data, String filename)
    {
        try
        {
            Context context = getApplicationContext();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));


            String auxData [] = data.split(";");
            for (int i = 0; i<auxData.length; i++)
                outputStreamWriter.write(auxData[i] +";");
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());

        }
    }
    /**********************FINE GESTIONE AGGIORNAMENTO*******************/


    //actions buttons
    //map
    public void openMap(View v)
    {
        TextView address = (TextView)findViewById(R.id.lblAddress);
        //passare anche indirizzo
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("address", address.getText());
        startActivity(intent);
    }

    //audio
    public void openAudio(View v)
    {
        audioPlayer("artwork" + String.valueOf(idArtwork));
    }

    //feedback
    public void openFeedback(View v)
    {
        TextView titleArtwork = (TextView)findViewById(R.id.lblTitleArtwork);
        Intent intent = new Intent(this, ReviewActivity.class);
        intent.putExtra("idArtwork", idArtwork);
        intent.putExtra("titleArtwork", titleArtwork.getText());
        startActivity(intent);
    }

    public void audioPlayer(String fileName)
    {
        if (isAudioPlaying)
        {
            mp.pause();
            isAudioPlaying = false;
        }
        else
        {
            try
            {
                int resID=getResources().getIdentifier(fileName, "raw", getPackageName());
                if (mp == null)
                    mp =MediaPlayer.create(this,resID);
                mp.start();
                isAudioPlaying = true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    /*****************************/

    /***
     * Funzioni utilizzate da ricerche correlate
     *
     */

    //click su una delle immagini delle ricerche correlate
    public void relatedSearchClick(View v)
    {
        String id = "";
        if (v.getId() == R.id.imgRelatedSearch1)
        {
            ImageView img1 = (ImageView) findViewById(R.id.imgRelatedSearch1);
            id = img1.getTag().toString();


        }
        else if (v.getId() == R.id.imgRelatedSearch2)
        {
            ImageView img2 = (ImageView) findViewById(R.id.imgRelatedSearch2);
            id = img2.getTag().toString();
        }
        else if (v.getId() == R.id.imgRelatedSearch3)
        {
            ImageView img3 = (ImageView) findViewById(R.id.imgRelatedSearch3);
            id = img3.getTag().toString();
        }
        idArtwork = Integer.valueOf(id);
        loadRelatedSearchRequest(id);
        //ScrollView mainScrollView = (ScrollView)findViewById(R.id.scrollView);
        //mainScrollView.fullScroll(ScrollView.FOCUS_UP);
    }

    /*
    * * Richieste AJAX per ricerche correlate
    * */
    private void loadRelatedSearchRequest(String id)
    {
        View v = findViewById(android.R.id.content);
        InviaRichiestaHttp request = new InviaRichiestaHttp(v, ScrollingActivity.this)
        {
            @Override
            protected void onPostExecute(String result) {
                if (result.contains("Exception"))
                    Toast.makeText(ScrollingActivity.this, result, Toast.LENGTH_SHORT).show();
                else
                {
                    initialSettings();
                    loadData(result);
                }
            }
        };
        request.execute("get", "oneArtworkMobile", id);
    }

    private void loadRelatedSearchMainRequest()
    {
        //ricerche correlate
        View v = findViewById(android.R.id.content);
        retVal = "nok";
        InviaRichiestaHttp request = new InviaRichiestaHttp(v, ScrollingActivity.this)
        {
            @Override
            protected void onPostExecute(String result) {
                if (result.contains("Exception"))
                {
                    Toast.makeText(ScrollingActivity.this, result, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //richiamo seconda intent passandogli come parametro la stringa json letta
                    retVal = String.valueOf(result);
                    JSONArray jsonArray = null;
                    if (retVal.compareTo("nok") != 0)
                    {
                        try
                        {
                            jsonArray = new JSONArray(retVal);
                            int i = 0;
                            JSONObject obj = null;
                            for (i = 0; i < jsonArray.length(); i++)
                            {
                                //tolgo la label con nessuna ricerca correlata
                                TextView textNoRelatedSearch = (TextView) findViewById(R.id.txtNoRelatedSearch);
                                textNoRelatedSearch.setVisibility(View.GONE);
                                try
                                {
                                    obj = jsonArray.getJSONObject(i);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                switch (i)
                                {
                                    case 0:
                                        loadRelatedSearch(obj, i);
                                        break;
                                    case 1:
                                        loadRelatedSearch(obj, i);
                                        break;
                                    case 2:
                                        loadRelatedSearch(obj, i);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        };
        String par = "?title=" + auxTitle + "&author=" + auxAuthor + "&artMovement=" + auxArtMovement +"&id="+auxId;
        request.execute("get", "similarArtworks", par);
    }

}
