function StatCard({
    title,
    value,
    subtitle,
    icon,
    type = "normal"
}) {

    return (
        <div className={`stat-card ${type}`}>

            <div className="stat-top">

                <div className="stat-icon">
                    {icon}
                </div>

                <span className="stat-title">
                    {title}
                </span>

            </div>

            <div className="stat-value">
                {value}
            </div>

            <div className="stat-subtitle">
                {subtitle}
            </div>

        </div>
    );
}

export default StatCard;