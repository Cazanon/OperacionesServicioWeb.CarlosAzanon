package com.dasm.carlosazanon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Consulta extends Activity {

	JSONArray registros;
	int numRegistros;
	int registroActual;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_consulta);
	
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		String datos = extras.getString("datos");

		try {
			registros = new JSONArray(datos);
			registroActual = 1;
			numRegistros = registros.getJSONObject(0).getInt("NUMREG");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mostrarRegistro(registroActual);		 
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
	}
	 
	private void mostrarRegistro(int numeroRegistro){
		try {
			TextView textIdentificadorRegistro = (TextView)findViewById(R.id.nRegistros);
			textIdentificadorRegistro.setText("Registro "+numeroRegistro+" de "+numRegistros);

			JSONObject registroMostrable = registros.getJSONObject(numeroRegistro);

			TextView txtDni = (TextView)findViewById(R.id.dniC);
			TextView txtNombre = (TextView)findViewById(R.id.nombreC);
			TextView txtApellidos = (TextView)findViewById(R.id.apellidoC);
			TextView txtDireccion = (TextView)findViewById(R.id.direccionC);
			TextView txtTelefono = (TextView)findViewById(R.id.telefonoC);
			TextView txtEquipo = (TextView)findViewById(R.id.equipoC);

			txtDni.setText(registroMostrable.getString("DNI"));
			txtDni.setEnabled(false);
			txtNombre.setText(registroMostrable.getString("Nombre"));
			txtNombre.setFocusable(false);
			txtApellidos.setText(registroMostrable.getString("Apellidos"));
			txtApellidos.setFocusable(false);
			txtDireccion.setText(registroMostrable.getString("Direccion"));
			txtDireccion.setFocusable(false);
			txtTelefono.setText(registroMostrable.getString("Telefono"));
			txtTelefono.setFocusable(false);
			txtEquipo.setText(registroMostrable.getString("Equipo"));
			txtEquipo.setFocusable(false);				
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	 
	 public void primero(View v) {
		 if(registroActual!=1){
			 registroActual=1;
			 mostrarRegistro(registroActual);
		 }
	 }

	 public void anterior(View v) {
		 if(registroActual!=1){
			 registroActual--;
			 mostrarRegistro(registroActual);
		 }
	 }

	 public void siguiente(View v) {
		 if(registroActual!=numRegistros){
			 registroActual++;
			 mostrarRegistro(registroActual);
		 }
	 }

	 public void ultimo(View v) {
		 if(registroActual!=numRegistros){
			 registroActual=numRegistros;
			 mostrarRegistro(registroActual);
		 }
	 }
	
}
