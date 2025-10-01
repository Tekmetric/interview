import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'

type WelcomeScreenProps = {
  onContinue: () => void
}

export const WelcomeScreen = ({ onContinue }: WelcomeScreenProps) => {
  return (
    <div className='flex min-h-screen items-center justify-center bg-gradient-to-br from-blue-50 via-white to-indigo-50'>
      <Card className='w-full max-w-lg shadow-xl'>
        <CardContent className='flex flex-col items-center gap-6 p-12 text-center'>
          <div className='flex flex-col gap-3'>
            <h1 className='text-5xl font-bold text-gray-900'>TekBoard</h1>
            <p className='text-lg text-gray-600'>Kanban workflow for auto repair shops</p>
          </div>

          <div className='flex flex-col gap-2 text-sm text-gray-500'>
            <p>✓ Real-time repair order tracking</p>
            <p>✓ Technician assignment & workload management</p>
            <p>✓ Status transitions with validation</p>
          </div>

          <Button onClick={onContinue} size='lg' className='w-full'>
            Continue as Demo User
          </Button>

          <p className='text-xs text-gray-400'>No authentication required for demo</p>
        </CardContent>
      </Card>
    </div>
  )
}
