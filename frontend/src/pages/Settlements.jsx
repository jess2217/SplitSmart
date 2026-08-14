import { ArrowRight, CheckCircle2 } from "lucide-react";

function Settlements({
    groups,
    groupData
}) {

    const settlements = [];

    groups.forEach((group) => {

        const data =
            groupData[group.id];

        if (!data) return;

        (data.settlements || [])
            .forEach((transaction) => {

                settlements.push({
                    ...transaction,
                    group
                });

            });
    });

    return (
        <div className="page">

            <div className="page-header">

                <div>

                    <p className="eyebrow">
                        SIMPLIFIED PAYMENTS
                    </p>

                    <h1>
                        Settlements
                    </h1>

                    <p className="page-description">
                        The simplest way to settle
                        everyone's balance.
                    </p>

                </div>

            </div>

            <section className="content-card">

                {settlements.length === 0 ? (

                    <div className="empty-state">

                        <CheckCircle2 size={32} />

                        <h3>
                            All settled
                        </h3>

                        <p>
                            No outstanding settlements
                            were found.
                        </p>

                    </div>

                ) : (

                    <div className="settlement-list">

                        {settlements.map(
                            (transaction, index) => (

                                <div
                                    className="settlement-row"
                                    key={index}
                                >

                                    <div className="settlement-person">

                                        <div className="member-avatar">
                                            {transaction.from
                                                ?.name
                                                ?.charAt(0)
                                                ?.toUpperCase()}
                                        </div>

                                        <strong>
                                            {transaction.from
                                                ?.name}
                                        </strong>

                                    </div>

                                    <ArrowRight />

                                    <div className="settlement-person">

                                        <div className="member-avatar">
                                            {transaction.to
                                                ?.name
                                                ?.charAt(0)
                                                ?.toUpperCase()}
                                        </div>

                                        <strong>
                                            {transaction.to
                                                ?.name}
                                        </strong>

                                    </div>

                                    <div className="settlement-amount">
                                        ₹
                                        {Number(
                                            transaction.amount
                                        ).toFixed(2)}
                                    </div>

                                    <span className="settlement-group">
                                        {transaction.group.name}
                                    </span>

                                </div>

                            )
                        )}

                    </div>

                )}

            </section>

        </div>
    );
}

export default Settlements;