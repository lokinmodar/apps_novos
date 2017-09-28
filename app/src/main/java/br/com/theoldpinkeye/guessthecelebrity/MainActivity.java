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
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    CountDownTimer timer;
    Resources res;
    String q;

    int min = 1;
    int max = 20;

    int acertosCont;
    int totalPerg;

    int calculo;
    boolean taRodando = false;


    TextView tempoRest;
    Button comecar;
    TextView acertos;
    TextView pergunta;
    TextView result;
    LinearLayout topo;
    LinearLayout rodape;
    GridLayout respostas;

    // Button buttonCrica; // preciso ver se vai mesmo precisar...

    ImageView downloadedImage;

    Button conta1;
    Button conta2;
    Button conta3;
    Button conta4;


    private String[] myString;
    private static final Random rgenerator = new Random();


    public void jogar(View view){

        comecar.setVisibility(View.INVISIBLE);
        topo.setVisibility(View.VISIBLE);
        result.setText("");
        rodape.setVisibility(View.VISIBLE);

        respostas.setVisibility(View.VISIBLE);
        acertosCont = 0;
        totalPerg = 0;
        acertos.setText("0/0");



        conta(view);

        timer =  new CountDownTimer(30*1000+100, 1000) {
            public void onTick(long millisUntilFinished){
                taRodando = true;
                tempoRest.setText(String.valueOf(millisUntilFinished/1000)+ " s");

            }
            @Override
            public void onFinish(){
                taRodando = false;
                tempoRest.setText("0 s");
                comecar.setVisibility(View.VISIBLE);
                topo.setVisibility(View.INVISIBLE);
                respostas.setVisibility(View.INVISIBLE);
                rodape.setVisibility(View.VISIBLE);
                comecar.setText("Play again!");
                result.setText("Score: "+Integer.toString(acertosCont)+"/"+Integer.toString(totalPerg));



            }


        }.start();




    }




    public void conta (View view) {

        myString = res.getStringArray(R.array.myArray);

        q = myString[rgenerator.nextInt(myString.length)];

        Random randomNum1 = new Random();
        Random randomNum2 = new Random();

        int num1 = randomNum1.nextInt(max - min + 1) + min;
        int num2 = randomNum2.nextInt(max - min + 1) + min;

        //     Log.i("Numero 1 = ", Integer.toString(num1));
        //   Log.i("Numero 2 = ", Integer.toString(num2));


        if (q.equals("/") && num2 > num1) {
            int numTemp = num1;
            num1 = num2;
            num2 = numTemp;
        }


        if (q.equals("+")) {
            pergunta.setText(Integer.toString(num1) + " " + q + " " + Integer.toString(num2));
            calculo = num1 + num2;
        } else if (q.equals("-")) {
            pergunta.setText(Integer.toString(num1) + " " + q + " " + Integer.toString(num2));
            calculo = num1 - num2;
        } else if (q.equals("*")) {
            pergunta.setText(Integer.toString(num1) + " " + q + " " + Integer.toString(num2));
            calculo = num1 * num2;
        } else if (q.equals("/") && num1 % num2 == 0) {
            pergunta.setText(Integer.toString(num1) + " " + q + " " + Integer.toString(num2));
            calculo = num1 / num2;

        } else {
            conta(view);
        }


        //   Log.i("Resultado ", Integer.toString(calculo));




        atualizaGrid(view);
    }

    public int geraRandom(){
        int minRand = 0;
        int maxRand = 256;

        Random rand = new Random();

        int randomRes = rand.nextInt(maxRand - minRand + 1) + minRand;
        while (randomRes == calculo){
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

                conta1.setText(Integer.toString(calculo));
                conta2.setText(Integer.toString(geraRandom()));
                conta3.setText(Integer.toString(geraRandom()));
                conta4.setText(Integer.toString(geraRandom()));

                break;
            case 2:

                conta2.setText(Integer.toString(calculo));
                conta1.setText(Integer.toString(geraRandom()));
                conta3.setText(Integer.toString(geraRandom()));
                conta4.setText(Integer.toString(geraRandom()));
                break;

            case 3:

                conta3.setText(Integer.toString(calculo));
                conta1.setText(Integer.toString(geraRandom()));
                conta2.setText(Integer.toString(geraRandom()));
                conta4.setText(Integer.toString(geraRandom()));

                break;

            case 4:

                conta4.setText(Integer.toString(calculo));
                conta1.setText(Integer.toString(geraRandom()));
                conta2.setText(Integer.toString(geraRandom()));
                conta3.setText(Integer.toString(geraRandom()));

                break;
        }






    }


    public void responder (View view){

        Button bostao = (Button) view;

        // int botaoApertado = Integer.parseInt(bostao.getTag().toString());


        //   Log.i("botao apertado", Integer.toString(botaoApertado));

        if (bostao.getText().equals(Integer.toString(calculo))){

            acertosCont += 1;
            totalPerg += 1;
            acertos.setText(Integer.toString(acertosCont)+"/"+Integer.toString(totalPerg));
            result.setText("Acertou!");

            conta(view);

        } else {
            totalPerg += 1;
            acertos.setText(Integer.toString(acertosCont)+"/"+Integer.toString(totalPerg));
            result.setText("Errou!");
            conta(view);
        }

    }


    public class DownloadTask extends AsyncTask<String, Void, String> {


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

    public void loga (View view){

        // http://img1.ak.crunchyroll.com/i/spire1/45e1fc2b8245696100f97e2e58f877b91488777525_full.jpg

        ImageDownload task = new ImageDownload();
        Bitmap minhaImagem;

        try {

            minhaImagem = task.execute("http://img1.ak.crunchyroll.com/i/spire1/45e1fc2b8245696100f97e2e58f877b91488777525_full.jpg").get();

            downloadedImage.setImageBitmap(minhaImagem);

        }catch (Exception e){

            e.printStackTrace();
        }
        Log.i("Mensagem:", "Manda ver!");
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

    public void copiaUrlNome(){ //pensar no como extrair os dados!
        String rio = "<div class=\"image\"><img src=\"http://cdn.posh24.se/images/:profile/c/50553\" alt=\"Prins Harry\"/></div>";
        Pattern p = Pattern.compile("src=\"(.*?)\"");
        Matcher m = p.matcher(rio);

        while (m.find()) {

            System.out.println(m.group(1));

        }
        p = Pattern.compile("alt=\"(.*?)\"");
        m = p.matcher(rio);

        while (m.find()) {

            System.out.println(m.group(1));

        }
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        downloadedImage = (ImageView)findViewById(R.id.imageView);
        comecar = (Button) findViewById(R.id.comecarButton);

        topo = (LinearLayout) findViewById(R.id.topoLayout);
        rodape = (LinearLayout) findViewById(R.id.rodapeLayout);
        respostas = (GridLayout) findViewById(R.id.buttonGrid) ;

        conta1 = (Button) findViewById(R.id.conta1);
        conta2 = (Button) findViewById(R.id.conta2);
        conta3 = (Button) findViewById(R.id.conta3);
        conta4 = (Button) findViewById(R.id.conta4);

        res = getResources();


        tempoRest = (TextView) findViewById(R.id.tempoTextView);
        acertos = (TextView) findViewById(R.id.acertosTextView);
        pergunta = (TextView) findViewById(R.id.pergunta);
        result = (TextView) findViewById(R.id.resultado);


        result.setText("");
        comecar.setVisibility(View.VISIBLE);
        topo.setVisibility(View.INVISIBLE);
        rodape.setVisibility(View.INVISIBLE);
        respostas.setVisibility(View.INVISIBLE);


        DownloadTask task = new DownloadTask();
        String result = null;

        try {
            result = task.execute("http://dsouzaesouza.000webhostapp.com/").get();

        } catch (InterruptedException e) {

            e.printStackTrace();

        } catch (ExecutionException e) {

            e.printStackTrace();
        }

        Log.i("Conteúdo da URL", result);
        Log.i("Length",String.valueOf(result.length()));

    }








}
