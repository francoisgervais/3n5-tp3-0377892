package com.example.tp3_0377892.SujetCreation

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.tp3_0377892.Main.MainActivity
import com.example.tp3_0377892.R
import com.example.tp3_0377892.SujetVote.VoteService
import com.example.tp3_0377892.databinding.ActivityCreationSujetBinding
import com.example.tp3_0377892.databinding.ActivityMainBinding
import com.example.tp3_0377892.databinding.NavHeaderBinding
import com.google.android.material.snackbar.Snackbar
import org.depinfo.sujetbd.UtilitaireBD

class SujetCreation : AppCompatActivity() {
    private lateinit var binding: ActivityCreationSujetBinding
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creation_sujet)

        binding = ActivityCreationSujetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left + v.paddingLeft,
                systemBars.top + v.paddingTop,
                systemBars.right + v.paddingRight,
                systemBars.bottom + v.paddingBottom
            )
            insets
        }

        title = "Créer un Sujet"

        val service = VoteService(UtilitaireBD.get(this.applicationContext))
        val inputField = findViewById<EditText>(R.id.editText)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val subjectContent = inputField.text.toString().trim()

            if (subjectContent.isEmpty()) {
                Toast.makeText(this, "Le sujet ne peut pas être vide.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isDuplicate = service.listeSujets().any { it.contenu == subjectContent }
            if (isDuplicate) {
                Toast.makeText(this, "Le sujet existe déjà.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                service.ajouterSujet(subjectContent)
                Toast.makeText(this, "Sujet ajouté avec succès!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }

        setupDrawer()
        setupDrawerApplicationBar()
    }

    private fun setupDrawer() {
        setupDrawerApplicationBar()
        setupDrawerItemSelected()
    }

    private fun setupDrawerApplicationBar() {
        // Afficher le menu hamburger sur la barre d'application
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Lier le tiroir de navigation à l'activité
        // R.string.ouvert et R.string.ferme sont des strings de ressource.
        // Référez-vous à la recette pour les strings de ressource pour voir comment les ajouter :
        // https://info.cegepmontpetit.ca/3N5-Prog3/recettes/ressources-string
        actionBarDrawerToggle = ActionBarDrawerToggle(this, binding.dlTiroir, R.string.ouvert, R.string.ferme)

        // Faire en sorte que le menu hamburger se transforme en flèche au clic, et vis versa
        binding.dlTiroir.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    private fun setupDrawerItemSelected() {
        val service : VoteService = VoteService(UtilitaireBD.get(this.getApplicationContext()))
        // Réagir aux clics sur les actions de menu
        binding.nvTiroir.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.accueil_item -> {
                    Snackbar.make(binding.root, "On va à l'accueil!", Snackbar.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.ajouter_item -> {
                    Snackbar.make(binding.root, "On va ajouter quelque chose!", Snackbar.LENGTH_SHORT).show()
                    val intent = Intent(this, SujetCreation::class.java)
                    startActivity(intent)
                }
                R.id.supprimer_votes -> {
                    Snackbar.make(binding.root, "On efface les votes!", Snackbar.LENGTH_SHORT).show()
                    service.supprimerTousLesVotes()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.supprimer_items -> {
                    Snackbar.make(binding.root, "On efface les sujets!", Snackbar.LENGTH_SHORT).show()
                    service.supprimerTousLesSujets()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
            false
        }
    }

    // Se déclenche lorsqu'un élément de la barre d'application est sélectionné
    // Exemple : lorsqu'on clique sur le menu hamburger
    // Peut aussi se combiner avec les autres options de menu qui se retrouvent dans la barre d'application
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Réagir au clic sur le menu hamburger
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item)
    }

    // Les deux méthodes suivantes permettent de synchroniser le menu hamburger
    // après avoir effectué une rotation de l'écran
    // Pour mieux comprendre :
    // 1. Commentez ces deux méthodes
    // 2. Relancer l'application
    // 3. Ouvrez le menu hamburger
    // 4. Tournez le péripérique pour qu'il soit en mode paysage
    // 5. Notez ce qui est arrivé au menu hambuger
    // 6. Recommencez, mais en décommentant les deux méthodes
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        actionBarDrawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        actionBarDrawerToggle.onConfigurationChanged(newConfig)
        super.onConfigurationChanged(newConfig)
    }

}