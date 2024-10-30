import type { PropsWithChildren } from 'react'

interface CardProps {
  title: string
  href: string
}

export const Card = ({
  title,
  children,
  href
}: PropsWithChildren<CardProps>): JSX.Element => (
  <a
    className='tek-group tek-rounded-lg tek-border tek-border-transparent tek-px-5 tek-py-4 tek-transition-colors hover:tek-border-neutral-700 hover:tek-bg-neutral-800/30'
    href={`${href}?utm_source=create-turbo&utm_medium=with-tailwind&utm_campaign=create-turbo"`}
    rel='noopener noreferrer'
    target='_blank'
  >
    <h2 className='tek-mb-3 tek-text-2xl tek-font-semibold'>
      {title}{' '}
      <span className='tek-inline-block tek-transition-transform group-hover:tek-translate-x-1 motion-reduce:tek-transform-none'>
        -&gt;
      </span>
    </h2>
    <p className='tek-m-0 tek-max-w-[30ch] tek-text-sm tek-opacity-50'>
      {children}
    </p>
  </a>
)
