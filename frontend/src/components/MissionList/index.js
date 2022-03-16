import { useQuery } from 'urql'
import gql from 'graphql-tag'
import MissionListItem from '../MissionListItem'
const MISSION_QUERY = gql`
query {
    launchesPast(limit: 10) {
      mission_name
      id
      launch_date_local
      launch_site {
        site_name_long
        site_name
      }
      links {
        article_link
        video_link
        mission_patch_small
      }
      ships {
        name
        home_port
        image
      }

      rocket {
        rocket_name
        second_stage {
            payloads {
              payload_type
              payload_mass_kg
            }
          } 
        }
    }
}
`
function Loading() {
    return (
        <div className="mx-auto w-full h-80 flex flex-col justify-center">
<svg
        className="spin mx-auto"
        style={{ height: "1rem" }}
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 24 24"
        stroke="black"
      >
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth={2}
          d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
        />
      </svg>
        </div>

    );
  }

const MissionList = () => {
    const [result] = useQuery({ query: MISSION_QUERY })
    const { data, fetching, error } = result
    
    if (fetching) return <Loading />

    if (error) return <div>Error</div>    
    const missions = data.launchesPast
  
    return (
    //   <div>
    //     {missions.map(mission => <MissionListItem key={mission.id} mission={mission} />)}
    //   </div>

    <div className="mt-8 flex flex-col">
    <div className="-my-2 -mx-4 overflow-x-auto sm:-mx-6 lg:-mx-8">
      <div className="inline-block min-w-full py-2 align-middle md:px-6 lg:px-8">
        <div className="overflow-hidden shadow ring-1 ring-black ring-opacity-5 md:rounded-lg">
          <table className="min-w-full divide-y divide-gray-300">
            <thead className="bg-gray-50">
              <tr>
                <th scope="col" className="py-3.5 pl-4 pr-3 text-left text-sm font-semibold text-gray-900 sm:pl-6">
                  Mission Name
                </th>
                <th scope="col" className="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                  Payload Details
                </th>
                <th scope="col" className="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                  Launched From
                </th>
                <th scope="col" className="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                  Local Launch Time
                </th>
                <th scope="col" className="px-3 py-3.5 text-left text-sm font-semibold text-gray-900">
                  Launch Media
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-200 bg-white">
            {missions.map(mission => <MissionListItem key={mission.id} mission={mission} />)}
            </tbody>
          </table>
        </div>
      </div>
    </div>
    </div>
    )
  }

  export {MissionList}