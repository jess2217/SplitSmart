import {
    ArrowDownLeft,
    ArrowUpRight,
    Wallet
} from "lucide-react";

function Balances({
    groups,
    groupData
}) {

    const rows = [];

    groups.forEach((group) => {

        const data =
            groupData[group.id];

        if (!data) return;

        Object.entries(
            data.balances || {}
        ).forEach(([student, amount]) => {

            rows.push({
                group,
                student,
                amount: Number(amount)
            });

        });
    });

    const owed =
        rows
            .filter((row) => row.amount > 0)
            .reduce(
                (sum, row) =>
                    sum + row.amount,
                0
            );

    const owing =
        rows
            .filter((row) => row.amount < 0)
            .reduce(
                (sum, row) =>
                    sum + Math.abs(row.amount),
                0
            );

    return (
        <div className="page">

            <div className="page-header">

                <div>

                    <p className="eyebrow">
                        MONEY FLOW
                    </p>

                    <h1>
                        Balances
                    </h1>

                    <p className="page-description">
                        Understand who owes money and
                        who should receive it.
                    </p>

                </div>

            </div>

            <div className="balance-summary">

                <div className="balance-summary-card positive">

                    <ArrowDownLeft size={22} />

                    <span>
                        Total owed
                    </span>

                    <strong>
                        ₹{owed.toFixed(2)}
                    </strong>

                </div>

                <div className="balance-summary-card negative">

                    <ArrowUpRight size={22} />

                    <span>
                        Total owing
                    </span>

                    <strong>
                        ₹{owing.toFixed(2)}
                    </strong>

                </div>

            </div>

            <section className="content-card">

                <div className="section-heading">

                    <div>
                        <p className="eyebrow">
                            ALL BALANCES
                        </p>

                        <h2>
                            Current balances
                        </h2>
                    </div>

                </div>

                {rows.length === 0 ? (

                    <div className="empty-state">

                        <Wallet size={30} />

                        <h3>
                            No balances yet
                        </h3>

                        <p>
                            Add an expense to calculate
                            group balances.
                        </p>

                    </div>

                ) : (

                    <div className="balance-list">

                        {rows.map((row, index) => (

                            <div
                                className="balance-row"
                                key={index}
                            >

                                <div>

                                    <strong>
                                        {row.student}
                                    </strong>

                                    <span>
                                        {row.group.name}
                                    </span>

                                </div>

                                <strong
                                    className={
                                        row.amount >= 0
                                            ? "amount-positive"
                                            : "amount-negative"
                                    }
                                >
                                    {row.amount >= 0
                                        ? "+"
                                        : "-"}
                                    ₹
                                    {Math.abs(
                                        row.amount
                                    ).toFixed(2)}
                                </strong>

                            </div>

                        ))}

                    </div>

                )}

            </section>

        </div>
    );
}

export default Balances;