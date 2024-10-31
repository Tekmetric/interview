import { Page } from '@tekmetric/components/page'
import { Card } from '@tekmetric/ui/card'

const DashboardPage = (): JSX.Element => (
  <Page>
    <Page.Title>Completed Questions</Page.Title>

    <Page.Body>
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
