package no.nav.pleiepengerbarn.uttak.regler

import no.nav.pleiepengerbarn.uttak.kontrakter.*


object KnekkpunktOppretter {


    fun finnKnekkpunkter(avklarteFakta: AvklarteFakta): List<Knekkpunkt> {
        val knekkpunkter = mutableListOf<Knekkpunkt>()

        knekkpunkter.addAll(finnKnekkpunkterForFerie(avklarteFakta.ferier))

        return knekkpunkter
    }

    private fun finnKnekkpunkterForFerie(ferier:List<LukketPeriode>): MutableList<Knekkpunkt> {
        val knekkpunkter = mutableListOf<Knekkpunkt>()
        for (ferie in ferier) {
            knekkpunkter.add(Knekkpunkt(ferie.fom, KnekkpunktType.FERIE))
            knekkpunkter.add(Knekkpunkt(ferie.tom.plusDays(1), KnekkpunktType.FERIE))
        }
        return knekkpunkter
    }

}