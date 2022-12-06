package com.msotor.implemqttv5;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ///sql
    private EditText et_codigo,et_descripcion,et_precio;
    private Button btfecha,bthora;
    private EditText efecha,ehora ;
    Button enviar;
    String men;
    Spinner spinner;
    ArrayList<String> listainfo;
    ArrayList<String> listaUsuario;

    private TextView TXT;
    String descripcion;

    //mqtt
     final String TAG = "Ssalas01";
     String topic = "Topic01";
     int i = 0;
     int qos = 0;
    //private String broker = "tcp://10.110.64.102:1883";

     String broker = "tcp://192.168.1.7:1883";
     String clientId = "AndroidAPPSalasSeba";
     MemoryPersistence persistence = new MemoryPersistence();
     MqttConnectionOptions mqttConnectionOptions = new MqttConnectionOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btfecha=findViewById(R.id.btt_fecha);
        bthora=findViewById(R.id.btt_hora);
        efecha=findViewById(R.id.edt_fecha);
        ehora=findViewById(R.id.edt_hora);
        bthora.setOnClickListener(this);
        btfecha.setOnClickListener(this);
        enviar=findViewById(R.id.bt_envi);
        TXT=findViewById(R.id.tv_conect);


        //sql
        et_codigo=findViewById(R.id.Edt_Codigo);
        et_descripcion=findViewById(R.id.Edt_Descripcion);
        et_precio=findViewById(R.id.Edt_Precio);

        //mqtt
        mqttConnectionOptions.setUserName("ssalas");
        mqttConnectionOptions.setPassword("1234".getBytes());
        mqttConnectionOptions.setCleanStart(false);
        mqttConnectionOptions.setAutomaticReconnect(true);


    }

    public void enviar(View view){

        TXT.setText("Conectado");
        AdminSQL admin = new AdminSQL(this,"administracion",null,1);
        SQLiteDatabase BaseDeDatos =admin .getWritableDatabase();

        String codigo=et_codigo.getText().toString();

        if (!codigo.isEmpty()){
            Cursor file=BaseDeDatos.rawQuery
                    ("select descripcion,precio,fecha,hora from articulos where codigo="+codigo,null);
            if (file.moveToFirst()){
                et_descripcion.setText(file.getString(0));
                et_precio.setText(file.getString(1));
                efecha.setText(file.getString(2));
                ehora.setText(file.getString(3));

                men="nombre es "+et_descripcion.getText().toString()+" su numero es "+ et_precio.getText().toString()+" su fecha es "+efecha.getText().toString()+" la hora es "+ehora.getText().toString();
                BaseDeDatos.close();
            }else {
                Toast.makeText(this,"no existe el articulo",Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this,"Debes introducior el codigo del articulos",Toast.LENGTH_LONG).show();
        }

        try {
            MqttClient mqttClient = new MqttClient(broker, clientId, persistence);
            mqttClient.setCallback(new MqttCallback() {


                @Override
                public void disconnected(MqttDisconnectResponse disconnectResponse) {

                }

                @Override
                public void mqttErrorOccurred(MqttException exception) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.i(TAG, "Mensaje recibido:" + message.toString());
                }

                @Override
                public void deliveryComplete(IMqttToken token) {

                }


                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    Log.i(TAG, "Conectado a: " + mqttClient.getCurrentServerURI());
                    String mensaje;


                    try {
                        mqttClient.subscribe(topic, qos);
                        Log.i(TAG, "Suscrito a: " + topic);

                        mqttClient.publish(topic, men.getBytes(), 0, false);
                        Toast.makeText(getApplicationContext(), "Mensaje publicado!", Toast.LENGTH_SHORT).show();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void authPacketArrived(int reasonCode, MqttProperties properties) {
                }

            });

            Log.i(TAG, "Conectando al broker: " + broker);
            mqttClient.connect(mqttConnectionOptions);



        } catch (MqttException me) {
            me.printStackTrace();
        }
    }
    public void Registar(View view){
        AdminSQL admin = new AdminSQL(this,"administracion",null,1);
        SQLiteDatabase BaseDeDatos =admin .getWritableDatabase();

        String codigo=et_codigo.getText().toString();
        descripcion=et_descripcion.getText().toString();
        String precio=et_precio.getText().toString();
        String fecha=efecha.getText().toString();
        String hora=ehora.getText().toString();

        if (!codigo.isEmpty()&& !descripcion.isEmpty()&&!precio.isEmpty() && !fecha.isEmpty() && !hora.isEmpty() ){
            ContentValues registro = new ContentValues();
            registro.put("codigo",codigo);
            registro.put("descripcion",descripcion);
            registro.put("precio",precio);
            registro.put("fecha",fecha);
            registro.put("hora",hora);

            BaseDeDatos.insert("articulos",null,registro);
            BaseDeDatos.close();

            et_codigo.setText("");
            et_precio.setText("");
            et_descripcion.setText("");
            efecha.setText("");
            ehora.setText("");

            Toast.makeText(this,"Registro Exitoso",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"Debes llenar todos los campos",Toast.LENGTH_LONG).show();
        }
    }
    public void buscar(View view){
        AdminSQL admin = new AdminSQL(this,"administracion",null,1);
        SQLiteDatabase BaseDeDatos =admin .getWritableDatabase();

        String codigo=et_codigo.getText().toString();

        if (!codigo.isEmpty()){
            Cursor file=BaseDeDatos.rawQuery
                    ("select descripcion,precio,fecha,hora from articulos where codigo="+codigo,null);

            if (file.moveToFirst()){
                et_descripcion.setText(file.getString(0));
                et_precio.setText(file.getString(1));
                efecha.setText(file.getString(2));
                ehora.setText(file.getString(3));
                BaseDeDatos.close();
            }else {
                Toast.makeText(this,"no existe el articulo",Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this,"Debes introducior el codigo del articulos",Toast.LENGTH_LONG).show();
        }
    }
    public void eliminar (View view){
        AdminSQL admin = new AdminSQL(this,"administracion",null,1);
        SQLiteDatabase BaseDeDatos =admin .getWritableDatabase();

        String codigo=et_codigo.getText().toString();

        if (!codigo.isEmpty()){
            int cantidad =BaseDeDatos.delete("articulos","codigo="+codigo,null);
            BaseDeDatos.close();

            et_codigo.setText("");
            et_precio.setText("");
            et_descripcion.setText("");
            efecha.setText("");
            ehora.setText("");
            if (cantidad==1){
                Toast.makeText(this,"Articulo eliminado ",Toast.LENGTH_LONG).show();
            }else {

                Toast.makeText(this, "Articulo  No  eliminado ", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this,"Debes introducir el codigo del articulo",Toast.LENGTH_LONG).show();
        }

    }
    public void modificar(View view){
        AdminSQL admin = new AdminSQL(this,"administracion",null,1);
        SQLiteDatabase BaseDeDatos =admin .getWritableDatabase();

        String codigo=et_codigo.getText().toString();
        String descripcion=et_descripcion.getText().toString();
        String precio=et_precio.getText().toString();
        String fecha=efecha.getText().toString();
        String hora =ehora.getText().toString();


        if (!codigo.isEmpty()&& !descripcion.isEmpty() && !precio.isEmpty() && !fecha.isEmpty() && !hora.isEmpty()){

            ContentValues registro = new ContentValues();
            registro.put("codigo",codigo);
            registro.put("descripcion",descripcion);
            registro.put("precio",precio);
            registro.put("fecha",fecha);
            registro.put("hora",hora);

            int cantidad=BaseDeDatos.update("articulos",registro,"codigo="+codigo,null);
            BaseDeDatos.close();

            if (cantidad==1){
                Toast.makeText(this,"Articulo modificado",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"El articulo no existe",Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this,"Debes de llenar todos los campos",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View v) {
        int dia,mes,ano,hora,minutos;
        if (v==btfecha){
            final Calendar c = Calendar.getInstance();
            dia=c.get(Calendar.DAY_OF_MONTH);
            mes=c.get(Calendar.MONTH);
            ano=c.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog= new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                    efecha.setText(dayOfMonth+"/"+monthOfYear+"/"+year);
                }
            },dia,mes,ano);
            datePickerDialog.show();

        }
        if (v==bthora){
            final Calendar c = Calendar.getInstance();
            hora=c.get(Calendar.HOUR_OF_DAY);
            minutos=c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                    ehora.setText(hourOfDay+":"+minute);
                }
            },hora,minutos,false);
            timePickerDialog.show();

        }
    }

    public void lis(View view){
        Intent intent = new Intent(this,MainActivityList.class);
        startActivity(intent);
    }
}