export function formatDate(dateString: string): string {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    timeZone: 'UTC',
  })
}

export function calculateTimeLeft(targetDate: number): {
  days: number
  hours: number
  minutes: number
  seconds: number
} {
  const now = Math.floor(Date.now() / 1000)
  const difference = targetDate - now

  if (difference <= 0) {
    return { days: 0, hours: 0, minutes: 0, seconds: 0 }
  }

  return {
    days: Math.floor(difference / (60 * 60 * 24)),
    hours: Math.floor((difference % (60 * 60 * 24)) / (60 * 60)),
    minutes: Math.floor((difference % (60 * 60)) / 60),
    seconds: Math.floor(difference % 60),
  }
}

export function cn(...classes: (string | boolean | undefined)[]): string {
  return classes.filter(Boolean).join(' ')
}

export function scrollToSection(sectionId: string): void {
  const section = document.getElementById(sectionId)
  if (section) {
    const yOffset = -40
    const y = section.getBoundingClientRect().top + window.pageYOffset + yOffset
    window.scrollTo({ top: y, behavior: 'smooth' })
  }
}
