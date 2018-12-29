package ar.edu.utn.frsf.isi.dam.laboratorio05;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.MyDatabase;
import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.Reclamo;
import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.ReclamoDao;

public class NuevoReclamoFragment extends Fragment {
    private static final int REQCODE_IMAGE_CAPTURE = 1;
    private static final int THUMBNAIL_WIDTH = 256;
    private static final int THUMBNAIL_HEIGHT = 256;

    public interface OnNuevoLugarListener {
        public void obtenerCoordenadas();
    }

    public void setListener(OnNuevoLugarListener listener) {
        this.listener = listener;
    }

    private Reclamo reclamoActual;
    private ReclamoDao reclamoDao;

    private EditText reclamoDesc;
    private EditText mail;
    private Spinner tipoReclamo;
    private TextView tvCoord;
    private Button buscarCoord;
    private Button btnGuardar;
    private ImageView imgFoto;
    private String pathFoto;
    private Drawable defaultDrawable;

    private ToggleButton toggleGrabar;
    private Button btnReproducir;
    private String pathAudio;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    private OnNuevoLugarListener listener;

    private ArrayAdapter<Reclamo.TipoReclamo> tipoReclamoAdapter;
    public NuevoReclamoFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        reclamoDao = MyDatabase.getInstance(this.getActivity()).getReclamoDao();

        View v = inflater.inflate(R.layout.fragment_nuevo_reclamo, container, false);

        reclamoDesc = (EditText) v.findViewById(R.id.reclamo_desc);
        mail= (EditText) v.findViewById(R.id.reclamo_mail);
        tipoReclamo= (Spinner) v.findViewById(R.id.reclamo_tipo);
        tvCoord= (TextView) v.findViewById(R.id.reclamo_coord);
        buscarCoord= (Button) v.findViewById(R.id.btnBuscarCoordenadas);
        btnGuardar= (Button) v.findViewById(R.id.btnGuardar);
        imgFoto = (ImageView) v.findViewById(R.id.imgFoto);
        toggleGrabar = (ToggleButton) v.findViewById(R.id.toggleGrabar);
        btnReproducir = (Button) v.findViewById(R.id.btnReproducir);
        btnReproducir.setEnabled(false);

        tipoReclamoAdapter = new ArrayAdapter<Reclamo.TipoReclamo>(getActivity(),android.R.layout.simple_spinner_item,Reclamo.TipoReclamo.values());
        tipoReclamoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoReclamo.setAdapter(tipoReclamoAdapter);

        pathFoto = "";
        pathAudio = "";
        mRecorder = null;
        defaultDrawable = imgFoto.getDrawable();

        int idReclamo =0;
        if(getArguments()!=null)  {
            idReclamo = getArguments().getInt("idReclamo",-1);
        }


        cargarReclamo(idReclamo);

        if(ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            toggleGrabar.setEnabled(false);
        }


        boolean edicionActivada = !tvCoord.getText().toString().equals("0;0");
        reclamoDesc.setEnabled(edicionActivada);
        reclamoDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                validarFormulario();
            }
        });
        mail.setEnabled(edicionActivada );
        tipoReclamo.setEnabled(edicionActivada);
        btnGuardar.setEnabled(edicionActivada);
        toggleGrabar.setEnabled(edicionActivada);
        imgFoto.setEnabled(edicionActivada);
        tipoReclamo.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validarFormulario();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                btnGuardar.setEnabled(false);
            }
        });

        buscarCoord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.obtenerCoordenadas();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveOrUpdateReclamo();
            }
        });

        imgFoto.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intentFoto.resolveActivity(getActivity().getPackageManager()) != null) {
                    File archivoFoto = null;
                    try {
                        archivoFoto = createFile("JPEG",".jpg");
                    }
                    catch(IOException e){
                        throw new RuntimeException("Error al crear imagen "+e.getMessage());
                    }
                    if(archivoFoto != null){
                        Uri uri = FileProvider
                                .getUriForFile(getActivity(),"com.example.android.fileprovider",archivoFoto);
                        intentFoto.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        pathFoto = archivoFoto.getAbsolutePath();
                        startActivityForResult(intentFoto, REQCODE_IMAGE_CAPTURE);
                    }
                }
            }
        });


        toggleGrabar.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){//Inicializo grabar
                    mRecorder = new MediaRecorder();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    try {
                        pathAudio = createFile("3GP",".3gp").getAbsolutePath();
                        mRecorder.setOutputFile(pathAudio);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    try {
                        mRecorder.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mRecorder.start();
                }
                else{//Finalizo grabar
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;
                    if(!pathAudio.isEmpty()) {
                        btnReproducir.setEnabled(true);
                    }
                    validarFormulario();
                }
            }
        });

        btnReproducir.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer = new MediaPlayer();
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mPlayer.release();
                        mPlayer = null;
                    }
                });
                try {
                    mPlayer.setDataSource(pathAudio);
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return v;
    }

    private void cargarReclamo(final int id){
        if( id >0){
            Runnable hiloCargaDatos = new Runnable() {
                @Override
                public void run() {
                    reclamoActual = reclamoDao.getById(id);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mail.setText(reclamoActual.getEmail());
                            tvCoord.setText(reclamoActual.getLatitud()+";"+reclamoActual.getLongitud());
                            reclamoDesc.setText(reclamoActual.getReclamo());
                            Reclamo.TipoReclamo[] tipos= Reclamo.TipoReclamo.values();
                            for(int i=0;i<tipos.length;i++) {
                                if(tipos[i].equals(reclamoActual.getTipo())) {
                                    tipoReclamo.setSelection(i);
                                    break;
                                }
                            }
                            pathFoto = reclamoActual.getPathFoto();
                            if(pathFoto == null) pathFoto = "";
                            if(!pathFoto.isEmpty()){
                                imgFoto.setImageBitmap(getThumbnail(pathFoto,THUMBNAIL_WIDTH,THUMBNAIL_HEIGHT));
                            }
                            pathAudio = reclamoActual.getPathAudio();
                            if(pathAudio == null) pathAudio = "";
                            if(!pathAudio.isEmpty()){
                                btnReproducir.setEnabled(true);
                            }
                        }
                    });
                }
            };
            Thread t1 = new Thread(hiloCargaDatos);
            t1.start();
        }else{
            String coordenadas = "0;0";
            if(getArguments()!=null) coordenadas = getArguments().getString("latLng","0;0");
            tvCoord.setText(coordenadas);
            reclamoActual = new Reclamo();
        }

    }

    private void saveOrUpdateReclamo(){
        reclamoActual.setEmail(mail.getText().toString());
        reclamoActual.setReclamo(reclamoDesc.getText().toString());
        reclamoActual.setTipo(tipoReclamoAdapter.getItem(tipoReclamo.getSelectedItemPosition()));
        reclamoActual.setPathFoto(pathFoto);
        reclamoActual.setPathAudio(pathAudio);

        if(tvCoord.getText().toString().length()>0 && tvCoord.getText().toString().contains(";")) {
            String[] coordenadas = tvCoord.getText().toString().split(";");
            reclamoActual.setLatitud(Double.valueOf(coordenadas[0]));
            reclamoActual.setLongitud(Double.valueOf(coordenadas[1]));
        }
        Runnable hiloActualizacion = new Runnable() {
            @Override
            public void run() {

                if(reclamoActual.getId()>0) reclamoDao.update(reclamoActual);
                else reclamoDao.insert(reclamoActual);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // limpiar vista
                        mail.setText(R.string.texto_vacio);
                        tvCoord.setText(R.string.texto_vacio);
                        reclamoDesc.setText(R.string.texto_vacio);
                        imgFoto.setImageDrawable(defaultDrawable);
                        pathFoto = "";
                        pathAudio = "";
                        btnReproducir.setEnabled(false);
                        getActivity().getFragmentManager().popBackStack();
                    }
                });
            }
        };
        Thread t1 = new Thread(hiloActualizacion);
        t1.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQCODE_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK){
            imgFoto.setImageBitmap(getThumbnail(pathFoto,THUMBNAIL_WIDTH,THUMBNAIL_HEIGHT));
            validarFormulario();
        }
    }

    private File createFile(String header,String format) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = header + "_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                format,         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }
    private Bitmap getThumbnail(String path,int width,int height){
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path),
                width, height);
        //La imagen la devuelve rotada 90 grados...
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        thumbnail = Bitmap.createBitmap(thumbnail,0,0,width,height,matrix,true);
        return thumbnail;
    }

    private void validarFormulario(){
        Reclamo.TipoReclamo tipo = (Reclamo.TipoReclamo) tipoReclamoAdapter.getItem(tipoReclamo.getSelectedItemPosition());
        if(tipo.equals(Reclamo.TipoReclamo.VEREDAS) || tipo.equals(Reclamo.TipoReclamo.CALLE_EN_MAL_ESTADO)){
            if(!pathFoto.isEmpty()){
                btnGuardar.setEnabled(true);
                return;
            }
        }
        else if (reclamoDesc.getText().length() >= 8 || !pathAudio.isEmpty()){
            btnGuardar.setEnabled(true);
            return;
        }
        btnGuardar.setEnabled(false);
    }
}
