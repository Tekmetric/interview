import { useLocation } from 'wouter'
import { WelcomeScreen } from '@/components/WelcomeScreen'

export const Welcome = () => {
  const [, setLocation] = useLocation()

  const handleContinue = () => {
    setLocation('/dashboard')
  }

  return <WelcomeScreen onContinue={handleContinue} />
}
