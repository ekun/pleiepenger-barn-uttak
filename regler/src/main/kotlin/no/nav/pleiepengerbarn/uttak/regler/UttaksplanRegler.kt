package no.nav.pleiepengerbarn.uttak.regler

import no.nav.pleiepengerbarn.uttak.kontrakter.*
import no.nav.pleiepengerbarn.uttak.regler.delregler.FerieRegel

internal object UttaksplanRegler {

    private val INGEN_UTBETALING = Prosent.ZERO
    private val FULL_UTBETALING = Prosent(100)

    fun fastsettUttaksplan(uttaksplan: Uttaksplan, grunnlag: RegelGrunnlag):Uttaksplan {
        val resultatPerioder = mutableListOf<Uttaksperiode>()
        uttaksplan.perioder.forEach { periode ->
                val resultat = kjørAlleRegler(uttaksperiode = periode, grunnlag = grunnlag)
                var oppdatertPeriode = periode.copy(uttaksperiodeResultat = resultat)
                oppdatertPeriode = oppdaterUtbetalingsgrad(oppdatertPeriode, grunnlag)
                resultatPerioder.add(oppdatertPeriode)
        }
        return uttaksplan.copy(perioder = resultatPerioder)
    }

    private fun kjørAlleRegler(uttaksperiode: Uttaksperiode, grunnlag: RegelGrunnlag):UttaksperiodeResultat {

        var oppdatertResultat = FerieRegel().kjør(uttaksperiode, grunnlag, uttaksperiode.uttaksperiodeResultat)

        return oppdatertResultat
    }

    private fun oppdaterUtbetalingsgrad(uttaksperiode: Uttaksperiode, grunnlag: RegelGrunnlag):Uttaksperiode {
        if (uttaksperiode.uttaksperiodeResultat.avslåttPeriodeÅrsaker.isEmpty()) {
            //TODO: Finn utbetalingsgrad, antar 100% i mellomtiden
            return uttaksperiode.copy(uttaksperiodeResultat = uttaksperiode.uttaksperiodeResultat.copy(utbetalingsgrad = FULL_UTBETALING))
        }
        return uttaksperiode.copy(uttaksperiodeResultat = uttaksperiode.uttaksperiodeResultat.copy(utbetalingsgrad = INGEN_UTBETALING))
    }

}