package no.nav.pleiepengerbarn.uttak.regler

import no.nav.pleiepengerbarn.uttak.kontrakter.*
import no.nav.pleiepengerbarn.uttak.regler.delregler.Avslått
import no.nav.pleiepengerbarn.uttak.regler.delregler.FerieRegel
import no.nav.pleiepengerbarn.uttak.regler.delregler.MedlemskapRegel
import no.nav.pleiepengerbarn.uttak.regler.delregler.SøkersDødRegel
import no.nav.pleiepengerbarn.uttak.regler.delregler.TilsynsbehovRegel
import no.nav.pleiepengerbarn.uttak.regler.domene.RegelGrunnlag

internal object UttaksplanRegler {

    private val NEDRE_GRENSE_FOR_UTTAK = Prosent(20)

    private val PeriodeRegler = linkedSetOf(
            MedlemskapRegel(),
            FerieRegel(),
            TilsynsbehovRegel()
    )

    private val UttaksplanRegler = linkedSetOf(
            SøkersDødRegel()
    )

    internal fun fastsettUtaksplan(
            grunnlag: RegelGrunnlag,
            knektePerioder: Map<LukketPeriode,Set<KnekkpunktType>>) : Uttaksplan {

        val perioder = mutableMapOf<LukketPeriode, UttaksPeriodeInfo>()

        knektePerioder.forEach { (periode, knekkpunktTyper) ->
            val avslagsÅrsaker = mutableSetOf<AvslåttPeriodeÅrsak>()
            PeriodeRegler.forEach { regel ->
                val utfall = regel.kjør(periode = periode, grunnlag = grunnlag)
                if (utfall is Avslått) {
                    avslagsÅrsaker.add(utfall.avslagsÅrsak)
                }
            }
            if (avslagsÅrsaker.isNotEmpty()) {
                perioder[periode] = AvslåttPeriode(
                        knekkpunktTyper = knekkpunktTyper,
                        avslagsÅrsaker = avslagsÅrsaker.toSet()
                )
            } else {
                val grader = GradBeregner.beregnGrader(periode, grunnlag)

                if (grader.grad < NEDRE_GRENSE_FOR_UTTAK) {
                    perioder[periode] = AvslåttPeriode(
                            knekkpunktTyper = knekkpunktTyper,
                            avslagsÅrsaker = setOf(AvslåttPeriodeÅrsak.FOR_LAV_UTTAKSGRAD)
                    )
                } else {
                    perioder[periode] = InnvilgetPeriode(
                            knekkpunktTyper = knekkpunktTyper,
                            grad = grader.grad,
                            utbetalingsgrader = grader.utbetalingsgrader

                    )
                }
            }
        }
        var uttaksplan = Uttaksplan(perioder = perioder)

        UttaksplanRegler.forEach {uttaksplanRegler ->
            uttaksplan = uttaksplanRegler.kjør(
                    uttaksplan = uttaksplan,
                    grunnlag = grunnlag
            )
        }
        return uttaksplan
    }

}