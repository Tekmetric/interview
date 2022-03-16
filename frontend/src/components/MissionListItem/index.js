import React from 'react'

const MissionListItem = ({ mission }) => {
let launch_date = new Date(mission.launch_date_local).toLocaleDateString(
    'en-gb',
    {
        
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    })
    let launch_time = new Date(mission.launch_date_local).toLocaleTimeString();
            return (
                <tr key={mission.id}>
                  <td className="whitespace-nowrap py-4 pl-4 pr-3 text-sm sm:pl-6">
                    <div className="flex items-center">
                      <div className="h-10 w-10 flex-shrink-0">
                        <img className="h-10 w-10 rounded-full" src={mission.links.mission_patch_small} alt="" />
                      </div>
                      <div className="ml-4">
                        <div className="font-medium text-gray-900">{mission.mission_name}</div>
                        <div className="text-gray-500">{mission.email}</div>
                      </div>
                    </div>
                  </td>
                  <td className="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                    <div className="text-gray-900">{mission.rocket.second_stage.payloads[0].payload_type}</div>
                    {mission.rocket.second_stage.payloads[0].payload_mass_kg ? <div className="text-gray-500">{mission.rocket.second_stage.payloads[0].payload_mass_kg}kg</div>: ""}
                  </td>
                  <td className="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                    <div className="text-gray-900 has-tooltip">
                    <span className='tooltip rounded shadow-lg p-1 bg-gray-100 -mt-8'>{mission.launch_site.site_name_long}</span>

                        {mission.launch_site.site_name}
                        </div>
                  </td>
                  <td className="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                    <div className="text-gray-900">{launch_time+ " " +launch_date}</div>
                  </td>    
                  <td className="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
                    {mission.links.video_link ? <a className="text-gray-900 block" href={mission.links.video_link}>Video</a>: ""}
                    {mission.links.article_link ? <a className="text-gray-900 block" href={mission.links.article_link}>Article</a>: ""}
                  </td>                  
                </tr>
)
            }
export default MissionListItem