import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { WELCOME_LABELS } from '@shared/constants'

type WelcomeScreenProps = {
  onContinue: () => void
}

export const WelcomeScreen = ({ onContinue }: WelcomeScreenProps) => {
  return (
    <div className='flex min-h-screen items-center justify-center bg-gradient-to-br from-blue-50 via-white to-indigo-50'>
      <Card className='w-full max-w-lg shadow-xl'>
        <CardContent className='flex flex-col items-center gap-6 p-12 text-center'>
          <div className='flex flex-col gap-3'>
            <h1 className='text-5xl font-bold text-gray-900'>{WELCOME_LABELS.TITLE}</h1>
            <p className='text-lg text-gray-600'>{WELCOME_LABELS.SUBTITLE}</p>
          </div>

          <div className='flex flex-col gap-2 text-sm text-gray-500'>
            <p>{WELCOME_LABELS.FEATURE_TRACKING}</p>
            <p>{WELCOME_LABELS.FEATURE_ASSIGNMENT}</p>
            <p>{WELCOME_LABELS.FEATURE_VALIDATION}</p>
          </div>

          <Button onClick={onContinue} size='lg' className='w-full'>
            {WELCOME_LABELS.DEMO_CTA}
          </Button>

          <p className='text-xs text-gray-400'>{WELCOME_LABELS.DEMO_SUBTITLE}</p>
        </CardContent>
      </Card>
    </div>
  )
}
