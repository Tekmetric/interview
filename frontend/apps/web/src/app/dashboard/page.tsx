import { Page } from '@tekmetric/components/page'
import { Button } from '@tekmetric/ui/button'
import { Card } from '@tekmetric/ui/card'
import { Icon } from '@tekmetric/ui/icon'

const DashboardPage = (): JSX.Element => (
  <Page>
    <Page.Title>Pending Questions</Page.Title>

    <Page.Body>
      <Card>
        <Card.Title
          actions={
            <>
              <Button variant='secondary' size='small'>
                <Icon icon='check' /> Resolve
              </Button>

              <Button size='small'>
                <Icon icon='chat' /> Answer
              </Button>
            </>
          }
        >
          Lorem ipsum dolor sit amet.
        </Card.Title>
        <Card.Info>Sergiu Butnarasu (2 days ago)</Card.Info>
        <Card.Body>
          Lorem ipsum dolor sit amet consectetur adipisicing elit. Quibusdam
          quisquam, excepturi consequuntur asperiores perspiciatis corporis
          facilis officiis impedit ad necessitatibus perferendis animi a
          delectus, itaque ut minus illum facere aliquam esse, voluptates
          voluptas voluptatem. Eius repudiandae, dolorum sed placeat laboriosam
          architecto at labore eaque optio accusamus impedit reprehenderit quae
          voluptate!
        </Card.Body>
      </Card>

      <Card>
        <Card.Title>Lorem ipsum dolor sit amet.</Card.Title>
        <Card.Info>Sergiu Butnarasu (2 days ago)</Card.Info>
        <Card.Body>
          Lorem ipsum dolor sit amet consectetur adipisicing elit. Quibusdam
          quisquam, excepturi consequuntur asperiores perspiciatis corporis
          facilis officiis impedit ad necessitatibus perferendis animi a
          delectus, itaque ut minus illum facere aliquam esse, voluptates
          voluptas voluptatem. Eius repudiandae, dolorum sed placeat laboriosam
          architecto at labore eaque optio accusamus impedit reprehenderit quae
          voluptate!
        </Card.Body>
      </Card>

      <Card>
        <Card.Title>Lorem ipsum dolor sit amet.</Card.Title>
        <Card.Info>Sergiu Butnarasu (2 days ago)</Card.Info>
        <Card.Body>
          Lorem ipsum dolor sit amet consectetur adipisicing elit. Quibusdam
          quisquam, excepturi consequuntur asperiores perspiciatis corporis
          facilis officiis impedit ad necessitatibus perferendis animi a
          delectus, itaque ut minus illum facere aliquam esse, voluptates
          voluptas voluptatem. Eius repudiandae, dolorum sed placeat laboriosam
          architecto at labore eaque optio accusamus impedit reprehenderit quae
          voluptate!
        </Card.Body>
      </Card>
    </Page.Body>
  </Page>
)

export default DashboardPage
