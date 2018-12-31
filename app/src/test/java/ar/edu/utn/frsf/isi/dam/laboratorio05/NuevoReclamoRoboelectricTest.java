package ar.edu.utn.frsf.isi.dam.laboratorio05;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.LatLng;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.Robolectric;

import java.util.Arrays;

import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.MyDatabase;
import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.Reclamo;

import static junit.framework.TestCase.assertEquals;

@RunWith(ParameterizedRobolectricTestRunner.class)
public class NuevoReclamoRoboelectricTest {
    @ParameterizedRobolectricTestRunner.Parameters(name = "Tipo = {0}")
    public static Iterable<Object[]>  data() {
        return Arrays.asList(new Object[][]{
                {Reclamo.TipoReclamo.RUIDOS_MOLESTOS},
                {Reclamo.TipoReclamo.VEREDAS},
                {Reclamo.TipoReclamo.CALLE_EN_MAL_ESTADO},
                {Reclamo.TipoReclamo.ILUMINACION},
                {Reclamo.TipoReclamo.RESIDUOS},
                {Reclamo.TipoReclamo.SEMAFOROS},
                {Reclamo.TipoReclamo.OTRO}
        });
    };

    private MainActivity activity;
    private NuevoReclamoFragment nuevoReclamoFragment;
    private EditText reclamoDesc;
    private EditText mail;
    private TextView tvCoord;
    private Button buscarCoord;
    private ToggleButton toggleGrabar;
    private Button btnReproducir;
    private Button btnGuardar;
    private Spinner tipoReclamo;
    private ImageView imgFoto;

    Reclamo.TipoReclamo tipoElegir;

    static final int milisegundos = 2000;

    public NuevoReclamoRoboelectricTest(Reclamo.TipoReclamo tipo){
        tipoElegir = tipo;
    }

    @Before
    public void setUp() throws Exception{
        activity = Robolectric.buildActivity(MainActivity.class).create().resume().get();
        activity.coordenadasSeleccionadas(new LatLng(50.0,50.0));

        nuevoReclamoFragment = (NuevoReclamoFragment)
                activity.getSupportFragmentManager()
                        .findFragmentByTag("nuevoReclamoFragment");

        View v = nuevoReclamoFragment.getView();
        reclamoDesc = (EditText) v.findViewById(R.id.reclamo_desc);
        mail= (EditText) v.findViewById(R.id.reclamo_mail);
        tvCoord= (TextView) v.findViewById(R.id.reclamo_coord);
        buscarCoord= (Button) v.findViewById(R.id.btnBuscarCoordenadas);
        toggleGrabar = (ToggleButton) v.findViewById(R.id.toggleGrabar);
        btnReproducir = (Button) v.findViewById(R.id.btnReproducir);
        btnGuardar = v.findViewById(R.id.btnGuardar);
        tipoReclamo = v.findViewById(R.id.reclamo_tipo);
        imgFoto = v.findViewById(R.id.imgFoto);
        seleccionarTipo(tipoElegir);
    }

    @After
    public void finish() throws  Exception{
        //Hay que cerrar la conexion de la bd despues de cada test.. o me tira excepcion
        //Fuente: https://github.com/robolectric/robolectric/issues/1890
        MyDatabase.reset();
    }


    private void seleccionarTipo(Reclamo.TipoReclamo tipoElegir){
        Reclamo.TipoReclamo[] tipos = Reclamo.TipoReclamo.values();
        for(int pos = 0;pos<tipos.length;pos++){
            if(tipos[pos].equals(tipoElegir)){
                tipoReclamo.setSelection(pos);
                break;
            }
        }
    }

    @Test
    public void testearSubeFoto() throws Exception{
        if(tipoElegir.equals(Reclamo.TipoReclamo.VEREDAS) || tipoElegir.equals(Reclamo.TipoReclamo.CALLE_EN_MAL_ESTADO)){
            nuevoReclamoFragment.setPathFoto("pathfoto");
            //"Retorna" de volver de la camara
            nuevoReclamoFragment.onActivityResult(NuevoReclamoFragment.REQCODE_IMAGE_CAPTURE,activity.RESULT_OK,null);

            assertEquals(true,btnGuardar.isEnabled());

            btnGuardar.performClick();
            Thread.sleep(milisegundos);
            Robolectric.flushForegroundThreadScheduler();

            assertEquals("",reclamoDesc.getText().toString());
            assertEquals("",mail.getText().toString());
            assertEquals("",nuevoReclamoFragment.getPathFoto());
            assertEquals("",nuevoReclamoFragment.getPathAudio());
        }
    }

    @Test
    public void testearNoSubeFoto() throws Exception{
        if(tipoElegir.equals(Reclamo.TipoReclamo.VEREDAS) || tipoElegir.equals(Reclamo.TipoReclamo.CALLE_EN_MAL_ESTADO)){
            Thread.sleep(milisegundos);
            Robolectric.flushForegroundThreadScheduler();
            assertEquals(false,btnGuardar.isEnabled());
        }
    }

    @Test
    public void testearConAudioSinDescripcionValida() throws  Exception{
        if(tipoElegir.equals(Reclamo.TipoReclamo.VEREDAS) || tipoElegir.equals(Reclamo.TipoReclamo.CALLE_EN_MAL_ESTADO)) return;
        nuevoReclamoFragment.setPathAudio("pathaudio");
        reclamoDesc.setText("123");
        btnGuardar.performClick();
        Thread.sleep(milisegundos);
        Robolectric.flushForegroundThreadScheduler();

        assertEquals("",reclamoDesc.getText().toString());
        assertEquals("",mail.getText().toString());
        assertEquals("",nuevoReclamoFragment.getPathFoto());
        assertEquals("",nuevoReclamoFragment.getPathAudio());
    }

    @Test
    public void testearConAudioConDescripcionValida() throws  Exception {
        if(tipoElegir.equals(Reclamo.TipoReclamo.VEREDAS) || tipoElegir.equals(Reclamo.TipoReclamo.CALLE_EN_MAL_ESTADO)) return;
        nuevoReclamoFragment.setPathAudio("pathaudio");
        reclamoDesc.setText("12345678");
        btnGuardar.performClick();

        Thread.sleep(milisegundos);
        Robolectric.flushForegroundThreadScheduler();

        assertEquals("",reclamoDesc.getText().toString());
        assertEquals("",mail.getText().toString());
        assertEquals("",nuevoReclamoFragment.getPathFoto());
        assertEquals("",nuevoReclamoFragment.getPathAudio());
    }

    @Test
    public void testearSinAudioSinDescripcionValida() throws  Exception{
        if(tipoElegir.equals(Reclamo.TipoReclamo.VEREDAS) || tipoElegir.equals(Reclamo.TipoReclamo.CALLE_EN_MAL_ESTADO)) return;
        reclamoDesc.setText("123");
        Thread.sleep(milisegundos);
        Robolectric.flushForegroundThreadScheduler();
        assertEquals(false,btnGuardar.isEnabled());
    }


    @Test
    public void testearSinAudioConDescripcionValida() throws  Exception{
        if(tipoElegir.equals(Reclamo.TipoReclamo.VEREDAS) || tipoElegir.equals(Reclamo.TipoReclamo.CALLE_EN_MAL_ESTADO)) return;
        reclamoDesc.setText("12345678");
        btnGuardar.performClick();
        Thread.sleep(milisegundos);
        Robolectric.flushForegroundThreadScheduler();
        assertEquals("",reclamoDesc.getText().toString());
        assertEquals("",mail.getText().toString());
        assertEquals("",nuevoReclamoFragment.getPathFoto());
        assertEquals("",nuevoReclamoFragment.getPathAudio());
    }
}
