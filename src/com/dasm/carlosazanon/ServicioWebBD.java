package com.dasm.carlosazanon;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ServicioWebBD extends Activity {

	private int INSERTAR=1;
	private int MODIFICAR=2;
	private int BORRAR=3;
	
	private EditText dni;
	private final String URL="http://demo.calamar.eui.upm.es/dasmapi/v1/miw04/fichas";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

		dni = (EditText)findViewById(R.id.dni);
        new Conexion().execute();
    }

    private class Conexion extends AsyncTask<String,Void,String>{
    
		@Override
		protected String doInBackground(String... params) {			
			String datos = "";
			try {
				AndroidHttpClient httpClient = AndroidHttpClient.newInstance("AndroidHttpClient");
				HttpGet httpGet = new HttpGet(URL);
				HttpResponse response = httpClient.execute(httpGet);
				datos = EntityUtils.toString(response.getEntity());
				httpClient.close();
			} catch (IOException e) {
				procesarError();
			}
			return datos;	
		}
		
		@Override
		protected void onPostExecute(String datos){
			try {
				JSONArray arrayDatos = new JSONArray(datos);
				int n=arrayDatos.getJSONObject(0).getInt("NUMREG");
				if(n==-1){
					procesarError();
					return;
				}
			}catch (Exception e){
				procesarError();
				return;
			}
			Toast.makeText(ServicioWebBD.this, "Conexion establecida", Toast.LENGTH_SHORT).show();
		}	
		
		private void procesarError(){
			Toast.makeText(ServicioWebBD.this, "Fallo al conectar", Toast.LENGTH_SHORT).show();
			deshabilitarFuncionalidad();
		}
    }
    
	private void deshabilitarFuncionalidad() {		
		TextView dni = (TextView)findViewById(R.id.dni);
		ImageButton consultar=(ImageButton)findViewById(R.id.consultar);
		ImageButton modificar=(ImageButton)findViewById(R.id.modificar);
		ImageButton insertar=(ImageButton)findViewById(R.id.insertar);
		ImageButton borrar=(ImageButton)findViewById(R.id.borrar);
		
		dni.setEnabled(false);
		consultar.setEnabled(false);
		modificar.setEnabled(false);
		insertar.setEnabled(false);
		borrar.setEnabled(false);		
	}
	
	public void goConsultar(View v){
    	new ConsultaBD().execute(dni.getText().toString()); 
    }
    
    public void goInsertar(View v){
    	new InsertarBD().execute(dni.getText().toString());
    }
    
    public void goModificar(View v){
    	new ModificarBD().execute(dni.getText().toString());	
    }
    
    public void goBorrar(View v){
    	new BorrarBD().execute(dni.getText().toString());
    }

    @Override
    public void onActivityResult(int actividad,int resultado,Intent datos){
   		String respuesta=datos.getStringExtra("respuesta");
   		Toast.makeText(ServicioWebBD.this, respuesta, Toast.LENGTH_SHORT).show();  	
    }
    
    private class ConsultaBD extends AsyncTask<String,Void,String>{
	//Parametro de la entrada
	//Parametro que manda a la interfaz (por ejemplo el progreso)
	//Parametro que devuelve el doInBackground y recibe el post execute
    	
    	//POST -> Inserta registro nuevo
    	//PUT  -> Actualizar registro
    	private ProgressDialog pDialog;
    	private boolean error;
    	
		@Override
		protected void onPreExecute() {			
			super.onPreExecute();
			error=false;
			pDialog = new ProgressDialog(ServicioWebBD.this);
			pDialog.setMessage(getString(R.string.progress_title));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... parametros) {
			String dni = parametros[0];
			String datos = "";
			String url_final = URL;
			if(!dni.equals("")){
				url_final+="/"+dni;
			}
			try {
				AndroidHttpClient httpClient = AndroidHttpClient.newInstance("AndroidHttpClient");
				HttpGet httpGet = new HttpGet(url_final);
				HttpResponse response = httpClient.execute(httpGet);
				datos = EntityUtils.toString(response.getEntity());
				httpClient.close();
			} catch (IOException e) {
				error=true;
				Log.e("Error en la operacion",e.toString());
			}
			return datos;			
		}
		
		@Override
		protected void onPostExecute(String info) {
			String mensaje = "";
			pDialog.dismiss();
			if(error) {
				mensaje = "La consulta genera un error";
				Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				JSONArray arrayDatos = new JSONArray(info);
				int numRegistros = arrayDatos.getJSONObject(0).getInt("NUMREG");
				switch(numRegistros) {
				case -1: 
					mensaje = "La consulta genera un error";
					Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
					break;
				case 0: 
					mensaje = "No existe un registro con ese DNI";
					Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
					break;
				default:
					Intent i = new Intent(ServicioWebBD.this, Consulta.class);
					i.putExtra("datos", info);
					startActivity(i);
				}
			} catch (Exception e) {
				mensaje = "La consulta genera un error de datos";
				Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
			}
		}    	
    }
    
    private class InsertarBD extends AsyncTask<String, Void, String>{

		private ProgressDialog pDialog;
		private boolean error;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			error = false;
			pDialog = new ProgressDialog(ServicioWebBD.this);
			pDialog.setMessage(getString(R.string.progress_title));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... parametros) {
			String dni = parametros[0];
			String datos = "";
			String url_final = URL;
			if(!dni.equals("")) {
				url_final += "/" + dni;
			} else {
				error = true;
			}
			try {
				AndroidHttpClient httpclient = AndroidHttpClient.newInstance("AndroidHttpClient");
				HttpGet httpget = new HttpGet(url_final);
				HttpResponse response = httpclient.execute(httpget);
				datos = EntityUtils.toString(response.getEntity());
				httpclient.close();
			} catch (IOException e) {
				error = true;
				Log.e("Error en la operacion", e.toString());
				e.printStackTrace();
			}
			return datos;            
		}

		@Override
		protected void onPostExecute(String datos) {
			String mensaje = "";
			pDialog.dismiss();
			if(error) {
				mensaje = "Introduzca un DNI para crear el registro";
				Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				JSONArray arrayDatos = new JSONArray(datos);
				int numRegistros = arrayDatos.getJSONObject(0).getInt("NUMREG");
				switch(numRegistros) {
				case -1: 
					mensaje = "La insercion genera un error";
					Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
					break;
				case 0:
					Intent i = new Intent(ServicioWebBD.this, Insercion.class);
					i.putExtra("dni", dni.getText().toString());
					i.putExtra("url",URL);
					startActivityForResult(i,INSERTAR);
					break;
				default: 
					mensaje = "Ya existe un registro con ese DNI";
					Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				mensaje = "La insercion genera un error de datos";
				Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
			}
		}

	}

	private class ModificarBD extends AsyncTask<String, Void, String>{

		private ProgressDialog pDialog;
		private boolean error;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			error = false;
			pDialog = new ProgressDialog(ServicioWebBD.this);
			pDialog.setMessage(getString(R.string.progress_title));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... parametros) {
			String dni = parametros[0];
			String datos = "";
			String url_final = URL;
			if(!dni.equals("")) {
				url_final += "/" + dni;
			} else {
				error = true;
			}
			try {
				AndroidHttpClient httpclient = AndroidHttpClient.newInstance("AndroidHttpClient");
				HttpGet httpget = new HttpGet(url_final);
				HttpResponse response = httpclient.execute(httpget);
				datos = EntityUtils.toString(response.getEntity());
				httpclient.close();
			} catch (IOException e) {
				error = true;
				Log.e("Error en la operacion", e.toString());
				e.printStackTrace();
			}
			return datos;            
		}

		@Override
		protected void onPostExecute(String datos) {
			String mensaje = "";
			pDialog.dismiss();
			if(error) {
				mensaje = "Introduzca un DNI para modificar el registro";
				Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				JSONArray arrayDatos = new JSONArray(datos);
				int numRegistros = arrayDatos.getJSONObject(0).getInt("NUMREG");
				switch(numRegistros) {
				case -1: 
					mensaje = "La modificacion genera un error";
					Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
					break;
				case 0: 
					mensaje = "No existe ningun registro con ese DNI";
					Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
					break;
				default: 
					Intent i = new Intent(ServicioWebBD.this, Modificacion.class);
					i.putExtra("datos", datos);
					i.putExtra("url", URL);
					startActivityForResult(i,MODIFICAR);
				}
			} catch (Exception e) {
				mensaje = "La modificacion genera un error de datos";
				Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
			}
		}

	}

	private class BorrarBD extends AsyncTask<String, Void, String>{

		private ProgressDialog pDialog;
		private boolean error;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			error = false;
			pDialog = new ProgressDialog(ServicioWebBD.this);
			pDialog.setMessage(getString(R.string.progress_title));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... parametros) {
			String dni = parametros[0];
			String datos = "";
			String url_final = URL;
			if(!dni.equals("")) {
				url_final += "/" + dni;
			} else {
				error = true;
			}
			try {
				AndroidHttpClient httpclient = AndroidHttpClient.newInstance("AndroidHttpClient");
				HttpGet httpget = new HttpGet(url_final);
				HttpResponse response = httpclient.execute(httpget);
				datos = EntityUtils.toString(response.getEntity());
				httpclient.close();
			} catch (IOException e) {
				error = true;
				Log.e("Error en la operacion", e.toString());
				e.printStackTrace();
			}
			return datos;            
		}

		@Override
		protected void onPostExecute(String datos) {
			String mensaje = "";

			pDialog.dismiss();
			if(error) {
				mensaje = "Introduzca un DNI para borrar el registro";
				Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				JSONArray arrayDatos = new JSONArray(datos);
				int numRegistros = arrayDatos.getJSONObject(0).getInt("NUMREG");
				switch(numRegistros) {
				case -1: 
					mensaje = "El borrado genera un error";
					Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
					break;
				case 0: 
					mensaje = "No existe un registro con ese DNI";
					Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
					break;
				default: 
					Intent i = new Intent(ServicioWebBD.this, Borrado.class);
					i.putExtra("datos", datos);
					i.putExtra("url", URL);
					startActivityForResult(i,BORRAR);
				}
			} catch (Exception e) {
				mensaje = "El borrado genera un error de datos";
				Toast.makeText(ServicioWebBD.this, mensaje, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
