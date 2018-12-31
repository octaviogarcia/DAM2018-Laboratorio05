package ar.edu.utn.frsf.isi.dam.laboratorio05;

import org.junit.Test;

import ar.edu.utn.frsf.isi.dam.laboratorio05.modelo.Reclamo;

import static org.junit.Assert.assertEquals;


public class NuevoReclamoUnitTest {
    NuevoReclamoFragment nuevoReclamoFragment;

    @Test
    public void veredasSubeFoto(){
        nuevoReclamoFragment = new NuevoReclamoFragment();
        Reclamo reclamo = new Reclamo();
        reclamo.setReclamo("Descripcion");
        reclamo.setEmail("Correo");
        reclamo.setId(999);
        reclamo.setPathAudio("pathaudio");
        reclamo.setPathFoto("pathfoto");
        reclamo.setLatitud(50.0);
        reclamo.setLongitud(50.0);
        reclamo.setTipo(Reclamo.TipoReclamo.VEREDAS);
        nuevoReclamoFragment.setReclamoActual(reclamo);
        assertEquals(nuevoReclamoFragment.esValido(),true);
    }

    @Test
    public void veredasNoSubeFoto(){
        nuevoReclamoFragment = new NuevoReclamoFragment();
        Reclamo reclamo = new Reclamo();
        reclamo.setReclamo("Descripcion");
        reclamo.setEmail("Correo");
        reclamo.setId(999);
        reclamo.setPathAudio("pathaudio");
        reclamo.setPathFoto("");
        reclamo.setLatitud(50.0);
        reclamo.setLongitud(50.0);
        reclamo.setTipo(Reclamo.TipoReclamo.VEREDAS);
        nuevoReclamoFragment.setReclamoActual(reclamo);
        assertEquals(nuevoReclamoFragment.esValido(),false);
    }

    @Test
    public void callesMalEstadoSubeFoto(){
        nuevoReclamoFragment = new NuevoReclamoFragment();
        Reclamo reclamo = new Reclamo();
        reclamo.setReclamo("Descripcion");
        reclamo.setEmail("Correo");
        reclamo.setId(999);
        reclamo.setPathAudio("pathaudio");
        reclamo.setPathFoto("pathfoto");
        reclamo.setLatitud(50.0);
        reclamo.setLongitud(50.0);
        reclamo.setTipo(Reclamo.TipoReclamo.CALLE_EN_MAL_ESTADO);
        nuevoReclamoFragment.setReclamoActual(reclamo);
        assertEquals(nuevoReclamoFragment.esValido(),true);
    }

    @Test
    public void callesMalEstadoNoSubeFoto(){
        nuevoReclamoFragment = new NuevoReclamoFragment();
        Reclamo reclamo = new Reclamo();
        reclamo.setReclamo("Descripcion");
        reclamo.setEmail("Correo");
        reclamo.setId(999);
        reclamo.setPathAudio("pathaudio");
        reclamo.setPathFoto("");
        reclamo.setLatitud(50.0);
        reclamo.setLongitud(50.0);
        reclamo.setTipo(Reclamo.TipoReclamo.CALLE_EN_MAL_ESTADO);
        nuevoReclamoFragment.setReclamoActual(reclamo);
        assertEquals(nuevoReclamoFragment.esValido(),false);
    }

    @Test
    public void demasTiposConDescripcionValidaSinAudio(){
        for(Reclamo.TipoReclamo tipo : Reclamo.TipoReclamo.values()){
            if(tipo.equals(Reclamo.TipoReclamo.CALLE_EN_MAL_ESTADO) || tipo.equals(Reclamo.TipoReclamo.VEREDAS)) continue;
            nuevoReclamoFragment = new NuevoReclamoFragment();
            Reclamo reclamo = new Reclamo();
            reclamo.setReclamo("12345678");
            reclamo.setEmail("Correo");
            reclamo.setId(999);
            reclamo.setPathAudio("");
            reclamo.setPathFoto("pathfoto");
            reclamo.setLatitud(50.0);
            reclamo.setLongitud(50.0);
            reclamo.setTipo(tipo);
            nuevoReclamoFragment.setReclamoActual(reclamo);
            assertEquals(nuevoReclamoFragment.esValido(),true);
        }
    }
    @Test
    public void demasTiposConDescripcionInvalidaSinAudio(){
        for(Reclamo.TipoReclamo tipo : Reclamo.TipoReclamo.values()){
            if(tipo.equals(Reclamo.TipoReclamo.CALLE_EN_MAL_ESTADO) || tipo.equals(Reclamo.TipoReclamo.VEREDAS)) continue;
            nuevoReclamoFragment = new NuevoReclamoFragment();
            Reclamo reclamo = new Reclamo();
            reclamo.setReclamo("123");
            reclamo.setEmail("Correo");
            reclamo.setId(999);
            reclamo.setPathAudio("");
            reclamo.setPathFoto("pathfoto");
            reclamo.setLatitud(50.0);
            reclamo.setLongitud(50.0);
            reclamo.setTipo(tipo);
            nuevoReclamoFragment.setReclamoActual(reclamo);
            assertEquals(nuevoReclamoFragment.esValido(),false);
        }
    }

    @Test
    public void demasTiposConDescripcionInvalidaConAudio(){
        for(Reclamo.TipoReclamo tipo : Reclamo.TipoReclamo.values()){
            if(tipo.equals(Reclamo.TipoReclamo.CALLE_EN_MAL_ESTADO) || tipo.equals(Reclamo.TipoReclamo.VEREDAS)) continue;
            nuevoReclamoFragment = new NuevoReclamoFragment();
            Reclamo reclamo = new Reclamo();
            reclamo.setReclamo("");
            reclamo.setEmail("Correo");
            reclamo.setId(999);
            reclamo.setPathAudio("pathaudio");
            reclamo.setPathFoto("pathfoto");
            reclamo.setLatitud(50.0);
            reclamo.setLongitud(50.0);
            reclamo.setTipo(tipo);
            nuevoReclamoFragment.setReclamoActual(reclamo);
            assertEquals(nuevoReclamoFragment.esValido(),true);
        }
    }
    @Test
    public void demasTiposConDescripcionValidaConAudio(){
        for(Reclamo.TipoReclamo tipo : Reclamo.TipoReclamo.values()){
            if(tipo.equals(Reclamo.TipoReclamo.CALLE_EN_MAL_ESTADO) || tipo.equals(Reclamo.TipoReclamo.VEREDAS)) continue;
            nuevoReclamoFragment = new NuevoReclamoFragment();
            Reclamo reclamo = new Reclamo();
            reclamo.setReclamo("12345678");
            reclamo.setEmail("Correo");
            reclamo.setId(999);
            reclamo.setPathAudio("pathaudio");
            reclamo.setPathFoto("pathfoto");
            reclamo.setLatitud(50.0);
            reclamo.setLongitud(50.0);
            reclamo.setTipo(tipo);
            nuevoReclamoFragment.setReclamoActual(reclamo);
            assertEquals(nuevoReclamoFragment.esValido(),true);
        }
    }
}
