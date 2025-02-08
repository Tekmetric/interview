'use client'

import { memo, useEffect, useRef, useCallback } from 'react'
import { createPortal } from 'react-dom'
import { motion, AnimatePresence } from 'framer-motion'
import Image from 'next/image'

import type { Rocket } from '@/app/types'

interface RocketModalProps {
  isOpen: boolean
  onClose: () => void
  rocket: Rocket
}

function RocketModal({
  isOpen,
  onClose,
  rocket,
}: RocketModalProps): React.ReactElement | null {
  const modalRef = useRef<HTMLDivElement>(null)
  const modalContainer =
    typeof document !== 'undefined'
      ? document.getElementById('modal-root') || document.body
      : null

  // Lock background scrolling when modal is open.
  useEffect(() => {
    if (isOpen) {
      const originalOverflow = document.body.style.overflow
      document.body.style.overflow = 'hidden'
      return () => {
        document.body.style.overflow = originalOverflow
      }
    }
  }, [isOpen])

  useEffect(() => {
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        onClose()
      }
    }
    if (isOpen) {
      document.addEventListener('keydown', handleEscape)
      return () => document.removeEventListener('keydown', handleEscape)
    }
  }, [isOpen, onClose])

  useEffect(() => {
    if (isOpen && modalRef.current) {
      modalRef.current.focus()
    }
  }, [isOpen])

  const handleContentClick = useCallback((e: React.MouseEvent) => {
    e.stopPropagation()
  }, [])

  if (!modalContainer) return null

  return createPortal(
    <AnimatePresence>
      {isOpen && (
        <motion.div
          className="fixed inset-0 z-50 bg-black/60 flex items-center justify-center p-4 "
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          transition={{ duration: 0.3 }}
          onClick={onClose}
          role="dialog"
          aria-modal="true"
          aria-labelledby="rocket-modal-title"
          data-testid="rocket-modal"
        >
          <motion.div
            ref={modalRef}
            onClick={handleContentClick}
            tabIndex={-1}
            className="bg-card rounded-lg shadow-lg w-full max-w-3xl max-h-[90vh] overflow-auto p-4 md:p-6 border-2 border-white"
            initial={{ y: 20, opacity: 0 }}
            animate={{ y: 0, opacity: 1 }}
            exit={{ y: 20, opacity: 0 }}
            transition={{ duration: 0.3 }}
            data-testid="rocket-modal-content"
          >
            {/* Header */}
            <header className="flex justify-between items-center mb-4">
              {rocket.name && (
                <h2
                  id="rocket-modal-title"
                  data-testid="rocket-modal-title"
                  className="text-2xl font-bold text-foreground"
                >
                  {rocket.name}
                </h2>
              )}
              <button
                onClick={onClose}
                className="text-3xl leading-none text-gray-500 focus:outline-none"
                data-testid="close-button-1"
              >
                &times;
              </button>
            </header>

            {/* Main Image */}
            {rocket.flickr_images && rocket.flickr_images.length > 0 && (
              <div
                className="relative w-full aspect-video mb-4"
                data-testid="rocket-image-container"
              >
                <Image
                  src={rocket.flickr_images[0] || '/images/placeholder.svg'}
                  alt={`Image of ${rocket.name}`}
                  fill
                  sizes="100vw"
                  className="object-cover rounded-lg"
                  data-testid="rocket-image"
                />
              </div>
            )}

            {/* Description */}
            {rocket.description && (
              <p
                className="text-muted-foreground mb-4"
                data-testid="rocket-description"
              >
                {rocket.description}
              </p>
            )}

            {/* Basic Info */}
            <section className="mb-4">
              <h3
                className="text-xl font-semibold text-foreground mb-2"
                data-testid="basic-info-title"
              >
                Basic Info
              </h3>
              <div className="grid grid-cols-2 gap-4">
                {rocket.height &&
                  rocket.height.meters !== undefined &&
                  rocket.height.feet !== undefined && (
                    <RocketDetailItem
                      label="Height"
                      value={`${rocket.height.meters}m / ${rocket.height.feet}ft`}
                      data-testid="rocket-height"
                    />
                  )}
                {rocket.diameter &&
                  rocket.diameter.meters !== undefined &&
                  rocket.diameter.feet !== undefined && (
                    <RocketDetailItem
                      label="Diameter"
                      value={`${rocket.diameter.meters}m / ${rocket.diameter.feet}ft`}
                      data-testid="rocket-diameter"
                    />
                  )}
                {rocket.mass &&
                  rocket.mass.kg !== undefined &&
                  rocket.mass.lb !== undefined && (
                    <RocketDetailItem
                      label="Mass"
                      value={`${rocket.mass.kg.toLocaleString()}kg / ${rocket.mass.lb.toLocaleString()}lb`}
                      data-testid="rocket-mass"
                    />
                  )}
                {rocket.stages !== undefined && (
                  <RocketDetailItem
                    label="Stages"
                    value={rocket.stages.toString()}
                    data-testid="rocket-stages"
                  />
                )}
                {rocket.boosters !== undefined && (
                  <RocketDetailItem
                    label="Boosters"
                    value={rocket.boosters.toString()}
                    data-testid="rocket-boosters"
                  />
                )}
                {rocket.first_flight && (
                  <RocketDetailItem
                    label="First Flight"
                    value={rocket.first_flight}
                    data-testid="rocket-first-flight"
                  />
                )}
                {rocket.company && (
                  <RocketDetailItem
                    label="Company"
                    value={rocket.company}
                    data-testid="rocket-company"
                  />
                )}
                {rocket.country && (
                  <RocketDetailItem
                    label="Country"
                    value={rocket.country}
                    data-testid="rocket-country"
                  />
                )}
                {rocket.cost_per_launch !== undefined && (
                  <RocketDetailItem
                    label="Cost per Launch"
                    value={`$${rocket.cost_per_launch.toLocaleString()}`}
                    data-testid="rocket-cost-per-launch"
                  />
                )}
                {rocket.success_rate_pct !== undefined && (
                  <RocketDetailItem
                    label="Success Rate"
                    value={`${rocket.success_rate_pct}%`}
                    data-testid="rocket-success-rate"
                  />
                )}
                {rocket.active !== undefined && (
                  <RocketDetailItem
                    label="Status"
                    value={rocket.active ? 'Active' : 'Inactive'}
                    valueClassName={
                      rocket.active ? 'text-green-400' : 'text-red-400'
                    }
                    data-testid="rocket-status"
                  />
                )}
              </div>
            </section>

            {/* First Stage Details */}
            {rocket.first_stage && (
              <section className="mb-4">
                <h3
                  className="text-xl font-semibold text-foreground mb-2"
                  data-testid="first-stage-title"
                >
                  First Stage
                </h3>
                <div className="grid grid-cols-2 gap-4">
                  {rocket.first_stage.engines !== undefined && (
                    <RocketDetailItem
                      label="Engines"
                      value={rocket.first_stage.engines.toString()}
                      data-testid="first-stage-engines"
                    />
                  )}
                  {rocket.first_stage.reusable !== undefined && (
                    <RocketDetailItem
                      label="Reusable"
                      value={rocket.first_stage.reusable ? 'Yes' : 'No'}
                      data-testid="first-stage-reusable"
                    />
                  )}
                  {rocket.first_stage.fuel_amount_tons !== undefined && (
                    <RocketDetailItem
                      label="Fuel Amount"
                      value={`${rocket.first_stage.fuel_amount_tons} tons`}
                      data-testid="first-stage-fuel-amount"
                    />
                  )}
                  {rocket.first_stage.burn_time_sec !== undefined && (
                    <RocketDetailItem
                      label="Burn Time"
                      value={`${rocket.first_stage.burn_time_sec} sec`}
                      data-testid="first-stage-burn-time"
                    />
                  )}
                  {rocket.first_stage.thrust_sea_level &&
                    rocket.first_stage.thrust_sea_level.kN !== undefined && (
                      <RocketDetailItem
                        label="Thrust (Sea Level)"
                        value={`${rocket.first_stage.thrust_sea_level.kN} kN`}
                        data-testid="first-stage-thrust-sea-level"
                      />
                    )}
                  {rocket.first_stage.thrust_vacuum &&
                    rocket.first_stage.thrust_vacuum.kN !== undefined && (
                      <RocketDetailItem
                        label="Thrust (Vacuum)"
                        value={`${rocket.first_stage.thrust_vacuum.kN} kN`}
                        data-testid="first-stage-thrust-vacuum"
                      />
                    )}
                </div>
              </section>
            )}

            {/* Second Stage Details */}
            {rocket.second_stage && (
              <section className="mb-4">
                <h3
                  className="text-xl font-semibold text-foreground mb-2"
                  data-testid="second-stage-title"
                >
                  Second Stage
                </h3>
                <div className="grid grid-cols-2 gap-4">
                  {rocket.second_stage.engines !== undefined && (
                    <RocketDetailItem
                      label="Engines"
                      value={rocket.second_stage.engines.toString()}
                      data-testid="second-stage-engines"
                    />
                  )}
                  {rocket.second_stage.reusable !== undefined && (
                    <RocketDetailItem
                      label="Reusable"
                      value={rocket.second_stage.reusable ? 'Yes' : 'No'}
                      data-testid="second-stage-reusable"
                    />
                  )}
                  {rocket.second_stage.fuel_amount_tons !== undefined && (
                    <RocketDetailItem
                      label="Fuel Amount"
                      value={`${rocket.second_stage.fuel_amount_tons} tons`}
                      data-testid="second-stage-fuel-amount"
                    />
                  )}
                  {rocket.second_stage.burn_time_sec !== undefined && (
                    <RocketDetailItem
                      label="Burn Time"
                      value={`${rocket.second_stage.burn_time_sec} sec`}
                      data-testid="second-stage-burn-time"
                    />
                  )}
                  {rocket.second_stage.thrust &&
                    rocket.second_stage.thrust.kN !== undefined && (
                      <RocketDetailItem
                        label="Thrust"
                        value={`${rocket.second_stage.thrust.kN} kN`}
                        data-testid="second-stage-thrust"
                      />
                    )}
                </div>
              </section>
            )}

            {/* Engine Details */}
            {rocket.engines && (
              <section className="mb-4">
                <h3
                  className="text-xl font-semibold text-foreground mb-2"
                  data-testid="engine-details-title"
                >
                  Engine Details
                </h3>
                <div className="grid grid-cols-2 gap-4">
                  {rocket.engines.type && (
                    <RocketDetailItem
                      label="Type"
                      value={rocket.engines.type}
                      data-testid="engines-type"
                    />
                  )}
                  {rocket.engines.version && (
                    <RocketDetailItem
                      label="Version"
                      value={rocket.engines.version}
                      data-testid="engines-version"
                    />
                  )}
                  {rocket.engines.layout && (
                    <RocketDetailItem
                      label="Layout"
                      value={rocket.engines.layout}
                      data-testid="engines-layout"
                    />
                  )}
                  {rocket.engines.number !== undefined && (
                    <RocketDetailItem
                      label="Number"
                      value={rocket.engines.number.toString()}
                      data-testid="engines-number"
                    />
                  )}
                  {rocket.engines.thrust_sea_level &&
                    rocket.engines.thrust_sea_level.kN !== undefined && (
                      <RocketDetailItem
                        label="Thrust (Sea Level)"
                        value={`${rocket.engines.thrust_sea_level.kN} kN`}
                        data-testid="engines-thrust-sea-level"
                      />
                    )}
                  {rocket.engines.thrust_vacuum &&
                    rocket.engines.thrust_vacuum.kN !== undefined && (
                      <RocketDetailItem
                        label="Thrust (Vacuum)"
                        value={`${rocket.engines.thrust_vacuum.kN} kN`}
                        data-testid="engines-thrust-vacuum"
                      />
                    )}
                  {rocket.engines.propellant_1 && (
                    <RocketDetailItem
                      label="Propellant 1"
                      value={rocket.engines.propellant_1}
                      data-testid="engines-propellant-1"
                    />
                  )}
                  {rocket.engines.propellant_2 && (
                    <RocketDetailItem
                      label="Propellant 2"
                      value={rocket.engines.propellant_2}
                      data-testid="engines-propellant-2"
                    />
                  )}
                </div>
              </section>
            )}

            {/* Landing Legs */}
            {rocket.landing_legs && (
              <section className="mb-4">
                <h3
                  className="text-xl font-semibold text-foreground mb-2"
                  data-testid="landing-legs-title"
                >
                  Landing Legs
                </h3>
                <div className="grid grid-cols-2 gap-4">
                  {rocket.landing_legs.number !== undefined && (
                    <RocketDetailItem
                      label="Number"
                      value={rocket.landing_legs.number.toString()}
                      data-testid="landing-legs-number"
                    />
                  )}
                  {rocket.landing_legs.material && (
                    <RocketDetailItem
                      label="Material"
                      value={rocket.landing_legs.material}
                      data-testid="landing-legs-material"
                    />
                  )}
                </div>
              </section>
            )}

            {/* Payload Weights */}
            {rocket.payload_weights && rocket.payload_weights.length > 0 && (
              <section className="mb-4">
                <h3
                  className="text-xl font-semibold text-foreground mb-2"
                  data-testid="payload-weights-title"
                >
                  Payload Weights
                </h3>
                <div className="space-y-2">
                  {rocket.payload_weights.map(
                    (payload, index) =>
                      payload && (
                        <div
                          key={payload.id || index}
                          className="border p-2 rounded"
                          data-testid={`payload-weight-${payload.id}`}
                        >
                          {payload.name && (
                            <div className="font-bold">{payload.name}</div>
                          )}
                          {(payload.kg !== undefined ||
                            payload.lb !== undefined) && (
                            <div>
                              {payload.kg !== undefined
                                ? payload.kg.toLocaleString()
                                : '-'}{' '}
                              kg /{' '}
                              {payload.lb !== undefined
                                ? payload.lb.toLocaleString()
                                : '-'}{' '}
                              lb
                            </div>
                          )}
                        </div>
                      )
                  )}
                </div>
              </section>
            )}

            <footer className="mt-6 flex flex-col md:flex-row justify-end items-center gap-4">
              {rocket.wikipedia && (
                <a
                  href={rocket.wikipedia}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-primary hover:text-primary/80 transition-colors duration-300"
                  data-testid="rocket-wikipedia"
                >
                  Wikipedia
                </a>
              )}
              <button
                className="btn btn-primary"
                onClick={onClose}
                data-testid="close-button-2"
              >
                Close
              </button>
            </footer>
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>,
    modalContainer
  )
}

const RocketDetailItem: React.FC<{
  label: string
  value: string
  valueClassName?: string
}> = memo(({ label, value, valueClassName = 'text-foreground' }) => (
  <div className="flex flex-col">
    <dt className="text-sm text-muted-foreground">{label}</dt>
    <dd className={`text-lg ${valueClassName}`}>{value}</dd>
  </div>
))
RocketDetailItem.displayName = 'RocketDetailItem'

export default memo(RocketModal)
