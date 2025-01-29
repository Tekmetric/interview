import { CountdownDisplay } from '../CountdownDisplay'

const withProviders = (Story: any) => (
  <div>
    <Story />
  </div>
)

export default {
  title: 'Components/CountdownDisplay',
  component: CountdownDisplay,
  decorators: [withProviders],
}

const timeLeftExample = {
  days: 1,
  hours: 2,
  minutes: 3,
  seconds: 4,
}

export const Default = () => <CountdownDisplay timeLeft={timeLeftExample} />
