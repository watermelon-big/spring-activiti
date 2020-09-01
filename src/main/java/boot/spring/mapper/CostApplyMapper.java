package boot.spring.mapper;


import boot.spring.po.CostApply;

public interface CostApplyMapper {
	void save(CostApply apply);

	CostApply getCostApply(int id);

	int updateByPrimaryKey(CostApply record);
}
