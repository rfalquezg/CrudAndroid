package com.example.proyecto3;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {
    ListView listUsuarios;
    ArrayAdapter<String> adapter;
    ArrayList<String> lista;

    ArrayList<Usuario> usuarios;

    DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        listUsuarios = findViewById(R.id.listUsuarios);
        dbHelper = new DBHelper(this);
        cargarUsuarios();

        listUsuarios.setOnItemClickListener((parent, view, position, id) -> {
            Usuario usuario = usuarios.get(position);
            mostrarDialogo(usuario);
        });
    }

        private void cargarUsuarios() {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM usuarios", null);
            lista = new ArrayList<>();
            usuarios = new ArrayList<>();

            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String nombre = cursor.getString(1);
                String email = cursor.getString(2);

                usuarios.add(new Usuario(id, nombre, email));
                lista.add(nombre + " - " + email);
            }
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,lista);
            listUsuarios.setAdapter(adapter);
        }
    private void mostrarDialogo(Usuario usuario) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar o Eliminar");

        final EditText inputNombre = new EditText(this);
        inputNombre.setText(usuario.getNombre());

        final EditText inputEmail = new EditText(this);
        inputEmail.setText(usuario.getEmail());

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(inputNombre);
        layout.addView(inputEmail);
        builder.setView(layout);

        builder.setPositiveButton("Actualizar", (dialog, which) -> {
            actualizarUsuario(usuario.id, inputNombre.getText().toString(), inputEmail.getText().toString());
        });

        builder.setNegativeButton("Eliminar", (dialog, which) -> {
            eliminarUsuario(usuario.id);
        });

        builder.setNeutralButton("Cancelar", null);
        builder.show();
    }

        private void actualizarUsuario(int id, String nombre, String email) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("nombre", nombre);
            values.put("email", email);

            db.update("usuarios", values, "id=?", new String[]{String.valueOf(id)});
            cargarUsuarios();
        }

        private void eliminarUsuario(int id) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("usuarios", "id=?", new String[]{String.valueOf(id)});
            cargarUsuarios();
        }

    }
