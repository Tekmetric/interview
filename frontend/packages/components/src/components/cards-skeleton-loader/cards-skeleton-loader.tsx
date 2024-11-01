import { Card } from '@tekmetric/ui/card'
import classNames from 'classnames'

import {
  CardsSkeletonLoaderClassNames,
  CardsSkeletonLoaderItemClassNames
} from './styles'

interface CardsSkeletonLoaderProps {
  cards?: number
}

export const CardsSkeletonLoader = ({
  cards = 5
}: CardsSkeletonLoaderProps): JSX.Element => (
  <div className={CardsSkeletonLoaderClassNames}>
    {Array.from({ length: cards }).map((_, index) => (
      // eslint-disable-next-line react/no-array-index-key -- This is a temporary solution
      <Card key={index}>
        <Card.Title>
          <span
            className={classNames(
              CardsSkeletonLoaderItemClassNames,
              'tek-w-[50%]'
            )}
          />
        </Card.Title>

        <Card.Info>
          <span
            className={classNames(
              CardsSkeletonLoaderItemClassNames,
              'tek-w-16'
            )}
          />
        </Card.Info>

        <Card.Body>
          <div className='tek-flex tek-flex-col tek-gap-2'>
            <span
              className={classNames(
                CardsSkeletonLoaderItemClassNames,
                'tek-w-full'
              )}
            />
            <span
              className={classNames(
                CardsSkeletonLoaderItemClassNames,
                'tek-w-full'
              )}
            />
            <span
              className={classNames(
                CardsSkeletonLoaderItemClassNames,
                'tek-w-24'
              )}
            />
          </div>
        </Card.Body>
      </Card>
    ))}
  </div>
)
