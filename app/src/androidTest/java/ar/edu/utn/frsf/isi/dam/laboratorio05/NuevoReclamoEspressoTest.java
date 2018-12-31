package ar.edu.utn.frsf.isi.dam.laboratorio05;


import android.app.Activity;
import android.os.Environment;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.Reclamo;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NuevoReclamoEspressoTest {
    static final int milisegundos = 2000;
    String imagepath = null;
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        mActivityRule.getActivity().coordenadasSeleccionadas(new LatLng(50.0, 50.0));


        InputStream in = mActivityRule.getActivity().getResources().openRawResource(R.raw.testimage);
        try {
            String path = mActivityRule.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/testimage.jpg";
            OutputStream out = new FileOutputStream(path);
            byte[] buff = new byte[1024];
            int read = 0;
            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
                out.close();
                imagepath = path;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void sinFotoCallesMalEstadoVeredas() throws Exception {
        //Seteo Mail
        onView(withId(R.id.reclamo_mail)).perform(typeText("Correo"),closeSoftKeyboard());
        //Seteo Descripcion
        onView(withId(R.id.reclamo_desc)).perform(typeText("12345678"),closeSoftKeyboard());

        //Click en spinner
        onView(withId(R.id.reclamo_tipo)).perform(click());
        //Eligo veredas
        onView(withText("VEREDAS")).perform(click());
        //Chequeo que esta elegido
        onView(withId(R.id.reclamo_tipo)).check(matches(withSpinnerText(containsString("VEREDAS"))));
        //Chequeo que el boton no esta habilitado
        onView(withId(R.id.btnGuardar)).check(matches(not(isEnabled())));

        //Idem para calle en mal estado
        onView(withId(R.id.reclamo_tipo)).perform(click());
        onView(withText("CALLE_EN_MAL_ESTADO")).perform(click());
        onView(withId(R.id.reclamo_tipo)).check(matches(withSpinnerText(containsString("CALLE_EN_MAL_ESTADO"))));
        onView(withId(R.id.btnGuardar)).check(matches(not(isEnabled())));
    }


    @Test
    public void conFotoCallesMalEstadoVeredas() throws Throwable {
        //Seteo Mail
        onView(withId(R.id.reclamo_mail)).perform(typeText("Correo"),closeSoftKeyboard());
        //Seteo Descripcion
        onView(withId(R.id.reclamo_desc)).perform(typeText("12345678"),closeSoftKeyboard());

        final NuevoReclamoFragment nuevoReclamoFragment = (NuevoReclamoFragment) mActivityRule.getActivity().getSupportFragmentManager().getFragments().get(1);

        nuevoReclamoFragment.setPathFoto(imagepath);

        //Se necesita correrlo en el hilo del UI por que se setea la foto programaticamente
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nuevoReclamoFragment.onActivityResult(NuevoReclamoFragment.REQCODE_IMAGE_CAPTURE, Activity.RESULT_OK, null);
            }
        });
        //Nose si hay race condition...
        Thread.sleep(1500);


        //Click en spinner
        onView(withId(R.id.reclamo_tipo)).perform(click());
        //Eligo veredas
        onView(withText("VEREDAS")).perform(click());
        //Chequeo que esta elegido
        onView(withId(R.id.reclamo_tipo)).check(matches(withSpinnerText(containsString("VEREDAS"))));
        //Chequeo que el boton esta habilitado
        onView(withId(R.id.btnGuardar)).check(matches(isEnabled()));

        //Idem para calle en mal estado
        onView(withId(R.id.reclamo_tipo)).perform(click());
        onView(withText("CALLE_EN_MAL_ESTADO")).perform(click());
        onView(withId(R.id.reclamo_tipo)).check(matches(withSpinnerText(containsString("CALLE_EN_MAL_ESTADO"))));
        onView(withId(R.id.btnGuardar)).check(matches(isEnabled()));
    }

    @Test
    public void conAudioDescripcionValida() throws Throwable {
        //Seteo Mail
        onView(withId(R.id.reclamo_mail)).perform(typeText("Correo"),closeSoftKeyboard());

        //Seteo Descripcion
        onView(withId(R.id.reclamo_desc)).perform(typeText("12345678"),closeSoftKeyboard());

        //Grabo 1 seg y chequeo que se habilito el boton reproducir
        onView(withId(R.id.toggleGrabar)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.toggleGrabar)).perform(click());
        onView(withId(R.id.btnReproducir)).check(matches(isEnabled()));

        //Para cada tipo de los que quedan chequeo que el boton guardar se habilita
        Reclamo.TipoReclamo[] tipos = Reclamo.TipoReclamo.values();
        for (Reclamo.TipoReclamo tipo : tipos) {
            if (tipo.equals(Reclamo.TipoReclamo.VEREDAS) || tipo.equals(Reclamo.TipoReclamo.CALLE_EN_MAL_ESTADO))
                continue;
            //Click en spinner
            onView(withId(R.id.reclamo_tipo)).perform(click());
            onView(withText(tipo.toString())).perform(click());
            //Chequeo que esta elegido
            onView(withId(R.id.reclamo_tipo)).check(matches(withSpinnerText(containsString(tipo.toString()))));
            //Chequeo que el boton esta habilitado
            onView(withId(R.id.btnGuardar)).check(matches(isEnabled()));
        }
    }

    @Test
    public void conAudioDescripcionInvalida() throws Throwable {
        //Seteo Mail
        onView(withId(R.id.reclamo_mail)).perform(typeText("Correo"),closeSoftKeyboard());

        //Seteo Descripcion
        onView(withId(R.id.reclamo_desc)).perform(typeText("123"),closeSoftKeyboard());

        //Grabo 1 seg y chequeo que se habilito el boton reproducir
        onView(withId(R.id.toggleGrabar)).perform(click());

        Thread.sleep(1000);
        onView(withId(R.id.toggleGrabar)).perform(click());
        onView(withId(R.id.btnReproducir)).check(matches(isEnabled()));

        //Para cada tipo de los que quedan chequeo que el boton guardar se habilita
        Reclamo.TipoReclamo[] tipos = Reclamo.TipoReclamo.values();
        for (Reclamo.TipoReclamo tipo : tipos) {
            if (tipo.equals(Reclamo.TipoReclamo.VEREDAS) || tipo.equals(Reclamo.TipoReclamo.CALLE_EN_MAL_ESTADO))
                continue;
            //Click en spinner
            onView(withId(R.id.reclamo_tipo)).perform(click());
            onView(withText(tipo.toString())).perform(click());
            //Chequeo que esta elegido
            onView(withId(R.id.reclamo_tipo)).check(matches(withSpinnerText(containsString(tipo.toString()))));
            //Chequeo que el boton esta habilitado
            onView(withId(R.id.btnGuardar)).check(matches(isEnabled()));
        }
    }

    @Test
    public void sinAudioDescripcionValida() throws Throwable {
        //Seteo Mail
        onView(withId(R.id.reclamo_mail)).perform(typeText("Correo"),closeSoftKeyboard());

        //Seteo Descripcion
        onView(withId(R.id.reclamo_desc)).perform(typeText("12345678"),closeSoftKeyboard());

        //Para cada tipo de los que quedan chequeo que el boton guardar se habilita
        Reclamo.TipoReclamo[] tipos = Reclamo.TipoReclamo.values();
        for (Reclamo.TipoReclamo tipo : tipos) {
            if (tipo.equals(Reclamo.TipoReclamo.VEREDAS) || tipo.equals(Reclamo.TipoReclamo.CALLE_EN_MAL_ESTADO))
                continue;
            //Click en spinner
            onView(withId(R.id.reclamo_tipo)).perform(click());
            onView(withText(tipo.toString())).perform(click());
            //Chequeo que esta elegido
            onView(withId(R.id.reclamo_tipo)).check(matches(withSpinnerText(containsString(tipo.toString()))));
            //Chequeo que el boton esta habilitado
            onView(withId(R.id.btnGuardar)).check(matches(isEnabled()));
        }
    }

    @Test
    public void sinAudioDescripcionInvalida() throws Throwable {
        //Seteo Mail
        onView(withId(R.id.reclamo_mail)).perform(typeText("Correo"),closeSoftKeyboard());

        //Seteo Descripcion
        onView(withId(R.id.reclamo_desc)).perform(typeText("123"),closeSoftKeyboard());

        //Para cada tipo de los que quedan chequeo que el boton guardar se habilita
        Reclamo.TipoReclamo[] tipos = Reclamo.TipoReclamo.values();
        for (Reclamo.TipoReclamo tipo : tipos) {
            if (tipo.equals(Reclamo.TipoReclamo.VEREDAS) || tipo.equals(Reclamo.TipoReclamo.CALLE_EN_MAL_ESTADO))
                continue;
            //Click en spinner
            onView(withId(R.id.reclamo_tipo)).perform(click());
            onView(withText(tipo.toString())).perform(click());
            //Chequeo que esta elegido
            onView(withId(R.id.reclamo_tipo)).check(matches(withSpinnerText(containsString(tipo.toString()))));
            //Chequeo que el boton esta deshabilitado
            onView(withId(R.id.btnGuardar)).check(matches(not(isEnabled())));
        }
    }
}