package br.com.theoldpinkeye.guessthecelebrity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    CountDownTimer timer;
    Resources res;
    String q;
    String resultadoSite;
    List<String> pessoas = new ArrayList<String>();
    List<String> imagemPessoas = new ArrayList<String>();

    ProgressBar carregando;
    //Tarefa barraProg;

    int acertosCont;
    int totalPerg;


    boolean taRodando = false;

    String nomePessoa;
    String urlImagem;

    int posNome;

    TextView tempoRest;
    Button comecar;
    TextView acertos;

    TextView result;
    LinearLayout topo;
    LinearLayout rodape;
    GridLayout respostas;



    ImageView downloadedImage;

    Button conta1;
    Button conta2;
    Button conta3;
    Button conta4;



    public void jogar(View view){

        comecar.setVisibility(View.INVISIBLE);
        topo.setVisibility(View.VISIBLE);
        result.setText("");
        rodape.setVisibility(View.VISIBLE);
        respostas.setVisibility(View.VISIBLE);
        acertosCont = 0;
        totalPerg = 0;
        acertos.setText("0/0");
        atualizaImagem(view);
        downloadedImage.setVisibility(View.VISIBLE);

        proximaPessoa(view);

        timer =  new CountDownTimer(60*1000+100, 1000) {
            public void onTick(long millisUntilFinished){
                taRodando = true;
                tempoRest.setText(String.valueOf(millisUntilFinished/1000)+ " s");

            }
            @Override
            public void onFinish(){
                taRodando = false;
                tempoRest.setText("0 s");
                result.setText("Total: "+Integer.toString(acertosCont)+"/"+Integer.toString(totalPerg));
                comecar.setVisibility(View.VISIBLE);
                topo.setVisibility(View.INVISIBLE);
                respostas.setVisibility(View.INVISIBLE);
                rodape.setVisibility(View.VISIBLE);
                downloadedImage.setVisibility(View.INVISIBLE);
                comecar.setText("De Novo!!");

            }


        }.start();




    }




    public void proximaPessoa (View view) {

        atualizaImagem(view);
        atualizaGrid(view);

    }


    public String fotoRandom(){
        Random myRandomizer = new Random();
        String fotoRandom = imagemPessoas.get(myRandomizer.nextInt(imagemPessoas.size()));
       // Log.i("url da foto", fotoRandom);

        posNome = imagemPessoas.indexOf(fotoRandom);
      //  Log.i("Pos:", Integer.toString(posNome));

        return fotoRandom;
    }

    public String pegaNome(int posicaoNome){

        String nomePessoa = pessoas.get(posicaoNome);

       // Log.i("Nome pessoa", nomePessoa);

        return nomePessoa;

    }

    public int geraRandom(){
        int minRand = 0;
        int maxRand = pessoas.size()-1;

        Random rand = new Random();

        int randomRes = rand.nextInt(maxRand - minRand + 1) + minRand;
        while (randomRes == posNome){
            randomRes = rand.nextInt(maxRand - minRand + 1) + minRand;
        }
        return randomRes;
    }



    public void atualizaGrid(View view) {

        Random randomTag = new Random();
        int minTag = 1;
        int maxTag = 4;

        int tag = randomTag.nextInt(maxTag - minTag + 1) + minTag;

        switch (tag){
            case 1:

                conta1.setText(nomePessoa);
                conta2.setText(pessoas.get(geraRandom()));
                conta3.setText(pessoas.get(geraRandom()));
                conta4.setText(pessoas.get(geraRandom()));

                break;
            case 2:

                conta2.setText(nomePessoa);
                conta1.setText(pessoas.get(geraRandom()));
                conta3.setText(pessoas.get(geraRandom()));
                conta4.setText(pessoas.get(geraRandom()));
                break;

            case 3:

                conta3.setText(nomePessoa);
                conta1.setText(pessoas.get(geraRandom()));
                conta2.setText(pessoas.get(geraRandom()));
                conta4.setText(pessoas.get(geraRandom()));

                break;

            case 4:

                conta4.setText(nomePessoa);
                conta1.setText(pessoas.get(geraRandom()));
                conta2.setText(pessoas.get(geraRandom()));
                conta3.setText(pessoas.get(geraRandom()));

                break;
        }






    }


    public void responder (View view){

        Button bostao = (Button) view;

         if (bostao.getText().equals(nomePessoa)){

            acertosCont += 1;
            totalPerg += 1;
            acertos.setText(Integer.toString(acertosCont)+"/"+Integer.toString(totalPerg));

             if (taRodando){
                 result.setText("Acertou!");
             } else {
                 result.setText("Total: "+Integer.toString(acertosCont)+"/"+Integer.toString(totalPerg));
             }

             proximaPessoa(view);

        } else {
            totalPerg += 1;
            acertos.setText(Integer.toString(acertosCont)+"/"+Integer.toString(totalPerg));
            if (taRodando){
                result.setText("Errou! Era " + nomePessoa +"!");
            } else {
                result.setText("Total: "+Integer.toString(acertosCont)+"/"+Integer.toString(totalPerg));
            }

            proximaPessoa(view);
        }

    }


    public class DownloadTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls/*params*/) {


            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;


            try {

                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection)url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1){

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;

            }
            catch(Exception e){

                e.printStackTrace();

                return "Failed";
            }
            // Log.i("URL", params[0]);
            //return "Done";
        }


    }




    public void atualizaImagem (View view){

        ImageDownload taskFoto = new ImageDownload();
        Bitmap minhaImagem;

        try {

            urlImagem = fotoRandom();
            minhaImagem = taskFoto.execute(urlImagem).get();
            nomePessoa = pegaNome(posNome);
            downloadedImage.setImageBitmap(minhaImagem);


        }catch (Exception e){

            e.printStackTrace();
        }
        //Log.i("Mensagem:", "Manda ver!");
    }

    public class ImageDownload extends AsyncTask<String, Void, Bitmap>{

        URL url;
        HttpURLConnection urlConnection = null;

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection)url.openConnection();

                urlConnection.connect();

                InputStream in = urlConnection.getInputStream();

                Bitmap minhaImagem = BitmapFactory.decodeStream(in);

                return minhaImagem;


            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();
            }

            return null;
        }
    }

    public void copiaUrlNome(){
        String endereco =  resultadoSite;


        Pattern p = Pattern.compile("src=\"(.*?)\"");
        Matcher m = p.matcher(endereco);

        while (m.find()) {

            if (m.group(1).contains("/cdn") && !m.group(1).contains("list")) {
                imagemPessoas.add(m.group(1));
            //    Log.i("End. imagem", m.group(1));
            }
        }

        p = Pattern.compile("alt=\"(.*?)\"");
        m = p.matcher(endereco);

        while (m.find()) {

                 pessoas.add(m.group(1));
           //     Log.i("Nome celeb", m.group(1));

        }
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        downloadedImage = (ImageView)findViewById(R.id.fotaImageView);
        comecar = (Button) findViewById(R.id.comecarButton);

        topo = (LinearLayout) findViewById(R.id.topoLayout);
        rodape = (LinearLayout) findViewById(R.id.rodapeLayout);
        respostas = (GridLayout) findViewById(R.id.buttonGrid) ;

        conta1 = (Button) findViewById(R.id.conta1);
        conta2 = (Button) findViewById(R.id.conta2);
        conta3 = (Button) findViewById(R.id.conta3);
        conta4 = (Button) findViewById(R.id.conta4);

        res = getResources();

//        carregando = (ProgressBar) findViewById(R.id.progressBar);
        tempoRest = (TextView) findViewById(R.id.tempoTextView);
        acertos = (TextView) findViewById(R.id.acertosTextView);
        result = (TextView) findViewById(R.id.resultado);

        result.setText("");

        comecar.setVisibility(View.VISIBLE);
        topo.setVisibility(View.INVISIBLE);
        rodape.setVisibility(View.INVISIBLE);
        respostas.setVisibility(View.INVISIBLE);
        downloadedImage.setVisibility(View.INVISIBLE);

        DownloadTask task = new DownloadTask();
        resultadoSite = null;

        try {
            resultadoSite = task.execute("http://www.posh24.se/kandisar/").get(); //dsouzaesouza.000webhostapp.com

        } catch (InterruptedException e) {

            e.printStackTrace();

        } catch (ExecutionException e) {

            e.printStackTrace();
        }

        //Log.i("Conteúdo da URL", resultadoSite);
        //Log.i("Length",String.valueOf(resultadoSite.length()));
        copiaUrlNome();

        Log.i("tamanho lista de nomes", Integer.toString(pessoas.size()));
        Log.i("tamanho lista de fotos", Integer.toString(imagemPessoas.size()));

    }








}
