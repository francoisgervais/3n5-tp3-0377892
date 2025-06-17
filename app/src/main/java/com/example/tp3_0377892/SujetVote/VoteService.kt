package com.example.tp3_0377892.SujetVote

import org.depinfo.sujetbd.Sujet
import org.depinfo.sujetbd.SujetVoteDAO
import org.depinfo.sujetbd.SujetVoteDataBase
import org.depinfo.sujetbd.Vote
import java.util.Optional

class VoteService(db: SujetVoteDataBase) {

    private val database : SujetVoteDataBase = db
    private val dao : SujetVoteDAO = database.dao()

    fun ajouterSujet(contenu: String){
        // validation du contenu
        if (contenu.length == 0){
            throw IllegalArgumentException("Sujet vide")
        }
        if (contenu.length < 5){
            throw IllegalArgumentException("Sujet trop court, 5 caractères minimum")
        }
        val existant : Optional<Sujet> = dao.sujetParContenu(contenu)
        if (existant.isPresent){
            throw IllegalArgumentException("Sujet déjà existant")
        }
        // Tout va bien, on peut ajouter le sujet dans la BD
        val sujet = Sujet()
        sujet.contenu = contenu
        dao.ajouterSujet(sujet)
    }

    fun ajouterVote(vote: Vote){
        if (vote.note !in 0..5) {
            throw IllegalArgumentException("La note doit être entre 0 et 5")
        }

        val existingVote: Optional<Vote> = dao.votePourCeSujetCeVotant(vote.sujetId, vote.nomVotant)
        if (existingVote.isPresent) {
            throw IllegalArgumentException("Ce votant a déjà voté pour ce sujet")
        }

        dao.ajouterVote(vote)
    }

    fun listeSujets(): List<Sujet>{
        return dao.tousLesSujets()
    }

    fun sujetsParOrdreDeVotes(): List<Sujet>{
        return dao.sujetsParOrdreDeVotes()
    }

    fun moyenneVotes(id: Long): Double{
        val votesForSujet = dao.votesPourCeSujet(id)
        return if (votesForSujet.isNotEmpty()) {
            votesForSujet.map { it.note }.average()
        } else {
            0.0
        }
    }

    fun distributionVotes(sujetId: Long): Map<Int, Int> {
        val votesForSujet = dao.votesPourCeSujet(sujetId)

        return votesForSujet.groupingBy { it.note }.eachCount()
    }

    fun supprimerTousLesSujets(){
        dao.supprimerTousLesSujets()
    }

    fun supprimerTousLesVotes(){
        dao.supprimerTousLesVotes()
    }

    fun sujetParId(id: Long): Sujet? {
        return dao.sujetParSonID(id)
    }

    fun ajouterSujetsPredefinis(sujets: List<String>) {
        sujets.forEach { sujet ->
            try {
                ajouterSujet(sujet)
            } catch (e: IllegalArgumentException) { }
        }
    }

    fun votePourCeSujetCeVotant(sujetId: Long, nomVotant: String): Optional<Vote> {
        return dao.votePourCeSujetCeVotant(sujetId, nomVotant)
    }

}