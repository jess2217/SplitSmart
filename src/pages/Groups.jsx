import { Plus } from "lucide-react";
import GroupCard from "../components/GroupCard";

function Groups({
    groups,
    groupData,
    onCreateGroup,
    onOpenGroup
}) {

    return (
        <div className="page">

            <div className="page-header">

                <div>
                    <p className="eyebrow">
                        MANAGEMENT
                    </p>

                    <h1>
                        Your Groups
                    </h1>

                    <p className="page-description">
                        Keep your trips, roommates and
                        shared expenses organised.
                    </p>
                </div>

                <button
                    className="primary-button"
                    onClick={onCreateGroup}
                >
                    <Plus size={18} />
                    New Group
                </button>

            </div>

            {groups.length === 0 ? (

                <div className="large-empty">
                    <h2>
                        No groups created yet
                    </h2>

                    <p>
                        Create a group and start
                        tracking shared expenses.
                    </p>

                    <button
                        className="primary-button"
                        onClick={onCreateGroup}
                    >
                        Create your first group
                    </button>
                </div>

            ) : (

                <div className="groups-page-grid">

                    {groups.map((group) => (

                        <GroupCard
                            key={group.id}
                            group={group}
                            members={
                                groupData[group.id]
                                    ?.members || []
                            }
                            expenses={
                                groupData[group.id]
                                    ?.expenses || []
                            }
                            onOpen={() =>
                                onOpenGroup(group.id)
                            }
                        />

                    ))}

                </div>

            )}

        </div>
    );
}

export default Groups;