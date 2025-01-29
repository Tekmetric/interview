'use client'

import { motion } from 'framer-motion'
import { useEffect, useRef, useState, useCallback, memo } from 'react'

import type { Rocket } from '@/app/types'

import RocketCard from './RocketCard'
import RocketModal from './RocketModal'

interface RocketDetailsProps {
  rockets: Rocket[]
}

function RocketDetails({ rockets }: RocketDetailsProps): React.ReactElement {
  const sectionRef = useRef<HTMLDivElement>(null)
  const [selectedRocket, setSelectedRocket] = useState<Rocket | null>(null)
  const [isOpen, setIsOpen] = useState(false)

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add('animate-fadeIn')
          }
        })
      },
      { threshold: 0.1 }
    )

    const rocketCards = document.querySelectorAll('.rocket-card')
    rocketCards.forEach((card, index) => {
      card.classList.add(`animate-delay-${index * 100}`)
      observer.observe(card)
    })

    if (sectionRef.current) {
      sectionRef.current.classList.add('animate-slideIn')
    }

    return () => observer.disconnect()
  }, [])

  const openModal = useCallback((rocket: Rocket) => {
    setSelectedRocket(rocket)
    setIsOpen(true)
  }, [])

  const closeModal = useCallback(() => {
    setIsOpen(false)
    setTimeout(() => setSelectedRocket(null), 500)
  }, [])

  return (
    <motion.section
      ref={sectionRef}
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 1 }}
      className="card mb-8"
      aria-labelledby="rocket-details-title"
      data-testid="rocket-details-section"
    >
      <h2
        id="rocket-details-title"
        className="section-title"
        data-testid="rocket-details-title"
      >
        SpaceX Rockets
      </h2>
      <div
        className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"
        data-testid="rocket-details-grid"
      >
        {rockets.map((rocket) => (
          <RocketCard key={rocket.id} rocket={rocket} onOpenModal={openModal} />
        ))}
      </div>
      {selectedRocket && (
        <RocketModal
          isOpen={isOpen}
          onClose={closeModal}
          rocket={selectedRocket}
          data-testid="rocket-modal"
        />
      )}
    </motion.section>
  )
}

export default memo(RocketDetails)
