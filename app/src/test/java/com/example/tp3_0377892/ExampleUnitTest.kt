package com.example.tp3_0377892

import com.example.tp3_0377892.SujetVote.VoteService
import org.depinfo.sujetbd.Vote
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import org.depinfo.sujetbd.Sujet
import org.depinfo.sujetbd.SujetVoteDAO
import org.depinfo.sujetbd.SujetVoteDataBase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.mockito.MockitoAnnotations
import java.util.Optional

class CreationSujetTest {

    private lateinit var dao: SujetVoteDAO
    private lateinit var service: VoteService

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        dao = mock()
        val database: SujetVoteDataBase = mock()
        whenever(database.dao()).thenReturn(dao)
        service = VoteService(database)
    }

    @Test
    fun `Ajout d'un nouveau sujet`() {
        // Arrange
        val contenu = "Un sujet valide"
        whenever(dao.sujetParContenu(contenu)).thenReturn(Optional.empty())

        // Act
        service.ajouterSujet(contenu)

        // Assert
        val captor = argumentCaptor<Sujet>()
        verify(dao).ajouterSujet(captor.capture())
        assertEquals(contenu, captor.firstValue.contenu)
    }

    @Test
    fun `Ajout d'un sujet avec exactement 5 charactères`() {
        val contenu = "Valid"
        whenever(dao.sujetParContenu(contenu)).thenReturn(Optional.empty())

        service.ajouterSujet(contenu)

        val captor = argumentCaptor<Sujet>()
        verify(dao).ajouterSujet(captor.capture())
        assertEquals(contenu, captor.firstValue.contenu)
    }

    @Test
    fun `Ajout de sujets uniques`() {
        val sujetsPredefinis = listOf("Sujet1", "Sujet2", "Sujet2", "Sujet3")
        whenever(dao.sujetParContenu("Sujet1")).thenReturn(Optional.empty())
        whenever(dao.sujetParContenu("Sujet2")).thenReturn(Optional.empty())
            .thenReturn(Optional.of(Sujet(id=2, contenu="Sujet2")))
        whenever(dao.sujetParContenu("Sujet3")).thenReturn(Optional.empty())

        service.ajouterSujetsPredefinis(sujetsPredefinis)

        verify(dao, times(1)).ajouterSujet(argThat { contenu == "Sujet1" })
        verify(dao, times(1)).ajouterSujet(argThat { contenu == "Sujet2" })
        verify(dao, times(1)).ajouterSujet(argThat { contenu == "Sujet3" })
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Exception si sujet vide`() {
        service.ajouterSujet("")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Exception si sujet trop court`() {
        service.ajouterSujet("abc")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Exception si sujet existe déjà`() {
        // Arrange
        val contenu = "Sujet existant"
        whenever(dao.sujetParContenu(contenu)).thenReturn(Optional.of(Sujet(1, contenu)))

        // Act
        service.ajouterSujet(contenu)
    }
}

class AjoutVoteTest {

    private lateinit var dao: SujetVoteDAO
    private lateinit var service: VoteService

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        dao = mock()
        val database: SujetVoteDataBase = mock()
        whenever(database.dao()).thenReturn(dao)
        service = VoteService(database)
    }

    @Test
    fun `Ajout d'un vote`() {
        // Arrange
        val vote = Vote(sujetId = 1, nomVotant = "Alice", note = 4)
        whenever(dao.votePourCeSujetCeVotant(1, "Alice")).thenReturn(Optional.empty())

        // Act
        service.ajouterVote(vote)

        // Assert
        val captor = argumentCaptor<Vote>()
        verify(dao).ajouterVote(captor.capture())
        assertEquals(vote, captor.firstValue)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Exception si vote hors limite`() {
        val vote = Vote(sujetId = 1, nomVotant = "Alice", note = 6)
        service.ajouterVote(vote)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Exception si voteur a déjà voté`() {
        // Arrange
        val vote = Vote(sujetId = 1, nomVotant = "Alice", note = 4)
        whenever(dao.votePourCeSujetCeVotant(1, "Alice")).thenReturn(Optional.of(vote))

        // Act
        service.ajouterVote(vote)
    }
}

class ResultatsTest {

    private lateinit var dao: SujetVoteDAO
    private lateinit var service: VoteService

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        dao = mock()
        val database: SujetVoteDataBase = mock()
        whenever(database.dao()).thenReturn(dao)
        service = VoteService(database)
    }

    @Test
    fun `Calcul de moyenne avec votes`() {
        // Arrange
        val votes = listOf(
            Vote(sujetId = 1, nomVotant = "Alice", note = 5),
            Vote(sujetId = 1, nomVotant = "Bob", note = 3),
            Vote(sujetId = 1, nomVotant = "Charlie", note = 4)
        )
        whenever(dao.votesPourCeSujet(1)).thenReturn(votes)

        // Act
        val average = service.moyenneVotes(1)

        // Assert
        assertEquals(4.0, average, 0.0)
    }

    @Test
    fun `Calcul de moyenne sans votes`() {
        // Arrange
        whenever(dao.tousLesVotes()).thenReturn(emptyList())

        // Act
        val average = service.moyenneVotes(1)

        // Assert
        assertEquals(0.0, average, 0.0)
    }

    @Test
    fun `Test de distribution avec votes`() {
        // Arrange
        val votes = listOf(
            Vote(sujetId = 1, nomVotant = "Alice", note = 5),
            Vote(sujetId = 1, nomVotant = "Bob", note = 3),
            Vote(sujetId = 1, nomVotant = "Charlie", note = 5),
            Vote(sujetId = 1, nomVotant = "David", note = 4),
            Vote(sujetId = 1, nomVotant = "Erwin", note = 3)
        )
        whenever(dao.votesPourCeSujet(1)).thenReturn(votes)

        // Act
        val distribution = service.distributionVotes(1)

        // Debugging print
        println("Distribution: $distribution")

        // Assert
        assertEquals(2, distribution[3])
        assertEquals(1, distribution[4])
        assertEquals(2, distribution[5])
    }

    @Test
    fun `Test de distribution sans votes`() {
        // Arrange
        whenever(dao.tousLesVotes()).thenReturn(emptyList())

        // Act
        val distribution = service.distributionVotes(1)

        // Debugging print
        println("Distribution: $distribution")

        // Assert
        assertTrue(distribution.isEmpty())
    }
}

class VariaTests {

    private lateinit var dao: SujetVoteDAO
    private lateinit var service: VoteService

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        dao = mock()
        val database: SujetVoteDataBase = mock()
        whenever(database.dao()).thenReturn(dao)
        service = VoteService(database)
    }

    @Test
    fun `Retrait des sujets`() {
        val sujets = listOf(Sujet(id = 1, contenu = "Test1"), Sujet(id = 2, contenu = "Test2"))
        whenever(dao.tousLesSujets()).thenReturn(sujets)
            .thenReturn(emptyList()) // after removal

        assertEquals(2, service.listeSujets().size)

        service.supprimerTousLesSujets()
        verify(dao).supprimerTousLesSujets()

        assertTrue(service.listeSujets().isEmpty())
    }

    @Test
    fun `Retrait des votes`() {
        val votes = listOf(
            Vote(sujetId = 1, nomVotant = "Alice", note = 5),
            Vote(sujetId = 1, nomVotant = "Bob", note = 3)
        )

        whenever(dao.votesPourCeSujet(1)).thenReturn(votes)

        val avgBefore = service.moyenneVotes(1)
        val distBefore = service.distributionVotes(1)

        assertEquals((5 + 3) / 2.0, avgBefore, 0.0)
        assertEquals(2, distBefore.values.sum())

        service.supprimerTousLesVotes()
        verify(dao).supprimerTousLesVotes()

        whenever(dao.votesPourCeSujet(1)).thenReturn(emptyList())

        val avgAfter = service.moyenneVotes(1)
        val distAfter = service.distributionVotes(1)

        assertEquals(0.0, avgAfter, 0.0)
        assertTrue(distAfter.isEmpty())

    }

    @Test
    fun `Test de tri de liste de sujet par votes`() {
        val sujet1 = Sujet(id = 1, contenu = "Sujet1")
        val sujet2 = Sujet(id = 2, contenu = "Sujet2")
        val sujet3 = Sujet(id = 3, contenu = "Sujet3")

        whenever(dao.sujetsParOrdreDeVotes()).thenReturn(listOf(sujet2, sujet3, sujet1))

        val votesForSujet1 = listOf(
            Vote(sujetId = 1, nomVotant = "Alice", note = 3),
            Vote(sujetId = 1, nomVotant = "Bob", note = 5)
        )

        val votesForSujet2 = listOf(
            Vote(sujetId = 2, nomVotant = "Charlie", note = 4),
            Vote(sujetId = 2, nomVotant = "David", note = 4),
            Vote(sujetId = 2, nomVotant = "Erwin", note = 2),
            Vote(sujetId = 2, nomVotant = "Fiona", note = 5),
            Vote(sujetId = 2, nomVotant = "George", note = 3)
        )

        val votesForSujet3 = listOf(
            Vote(sujetId = 3, nomVotant = "Helen", note = 5),
            Vote(sujetId = 3, nomVotant = "Ian", note = 1),
            Vote(sujetId = 3, nomVotant = "Jane", note = 4)
        )

        whenever(dao.votesPourCeSujet(1)).thenReturn(votesForSujet1)
        whenever(dao.votesPourCeSujet(2)).thenReturn(votesForSujet2)
        whenever(dao.votesPourCeSujet(3)).thenReturn(votesForSujet3)

        val sortedSubjects = service.sujetsParOrdreDeVotes()

        assertEquals("Sujet2", sortedSubjects[0].contenu)
        assertEquals("Sujet3", sortedSubjects[1].contenu)
        assertEquals("Sujet1", sortedSubjects[2].contenu)

        verify(dao).sujetsParOrdreDeVotes()
    }
}