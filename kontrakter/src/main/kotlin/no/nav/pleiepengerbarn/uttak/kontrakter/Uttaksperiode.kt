package no.nav.pleiepengerbarn.uttak.kontrakter

data class Uttaksperiode(
        val periode:LukketPeriode,
        val knekkpunktTyper:Set<KnekkpunktType> = setOf(),
        val uttaksperiodeResultat: UttaksperiodeResultat = UttaksperiodeResultat(grad = Prosent(0))

) {
    fun knekk(knekkpunktListe: List<Knekkpunkt>): List<Uttaksperiode>{
        val knekteUttaksperioder = mutableListOf<Uttaksperiode>()
        var rest = this
        knekkpunktListe.forEach {
            val dato = it.knekk
            if (!dato.isEqual(rest.periode.fom) && !dato.isBefore(rest.periode.fom) && !dato.isAfter(rest.periode.tom)) {
                knekteUttaksperioder.add(Uttaksperiode(periode = LukketPeriode(rest.periode.fom, dato.minusDays(1)), knekkpunktTyper = rest.knekkpunktTyper))
                rest = Uttaksperiode(periode = LukketPeriode(dato, rest.periode.tom), knekkpunktTyper = it.typer)
            }
        }
        knekteUttaksperioder.add(rest)
        return knekteUttaksperioder
    }

}