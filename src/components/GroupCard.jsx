import {
    ArrowUpRight,
    Users
} from "lucide-react";

function GroupCard({
    group,
    members = [],
    expenses = [],
    onOpen
}) {

    const total =
        expenses.reduce(
            (sum, expense) =>
                sum + Number(expense.amount || 0),
            0
        );

    return (
        <div className="group-card">

            <div className="group-card-header">

                <div className="group-avatar">
                    {group.name
                        ?.charAt(0)
                        ?.toUpperCase()}
                </div>

                <button
                    className="round-button"
                    onClick={onOpen}
                >
                    <ArrowUpRight size={18} />
                </button>

            </div>

            <h3>
                {group.name}
            </h3>

            <div className="group-meta">

                <span>
                    <Users size={14} />
                    {members.length} members
                </span>

                <span>
                    ₹{total.toFixed(2)} spent
                </span>

            </div>

        </div>
    );
}

export default GroupCard;